package com.dari.endoftheworld.catastrophe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Earthquake disaster modeled loosely on how real earthquakes behave, not
 * just a generic shake effect:
 * <p>
 * - Single epicenter (a random online player's position at trigger time).
 *   Everyone else's intensity falls off with distance from it, like real
 *   seismic attenuation — simplified to a linear falloff rather than a full
 *   physical model, which isn't worth the complexity here.
 * - Peak intensity near the start of the shake, tapering off over its
 *   duration — real quakes hit hardest in the first moments and decay, they
 *   don't shake at a constant level throughout.
 * - Two kinds of ground failure per pulse: collapse below the player
 *   (ground giving way) and above (cave-ins/falling debris — a real and
 *   often more dangerous earthquake hazard underground, and directly
 *   relevant since Minecraft players dig).
 * - Falling debris can actually hurt you (hurtEntities on the spawned
 *   FallingBlockEntity), only above a minimum intensity so weak tremors
 *   stay harmless.
 * - Aftershocks: real earthquakes are reliably followed by smaller ones.
 *   After a shake ends (if it was strong enough to matter), a weaker
 *   aftershock is scheduled 5-15s later from the same epicenter.
 * <p>
 * UNVERIFIED PART OF THIS FILE: FallingBlockEntity.hurtEntities is accessed
 * as a public field below. Multiple Yarn-mapped API listings show this field
 * as private in Fabric's mappings, but Forge's official (Mojang) mappings
 * for the same field have historically differed in visibility, and I
 * couldn't confirm which applies to this exact build. If gradlew build
 * fails on that line specifically, it likely needs a setter method instead
 * of direct field access — send the error and I'll fix it.
 */
public final class EarthquakeDisaster implements Disaster {

    public static final EarthquakeDisaster INSTANCE = new EarthquakeDisaster();

    private static final Random RANDOM = new Random();
    private static final int BASE_SHAKE_TICKS = 40; // ~2s at full magnitude
    private static final int PULSE_EVERY_TICKS = 4;
    private static final int MAX_RADIUS = 80; // blocks from epicenter still felt at all
    private static final int CANDIDATE_POSITIONS_PER_PULSE = 6;
    private static final int COLLAPSE_RADIUS = 3;
    private static final double MIN_MAGNITUDE_TO_DAMAGE = 0.5;
    private static final double MIN_MAGNITUDE_FOR_AFTERSHOCK = 0.3;

    private final Map<UUID, ShakeState> shakingPlayers = new HashMap<>();
    private final List<PendingAftershock> pendingAftershocks = new ArrayList<>();

    private EarthquakeDisaster() {
    }

    @Override
    public void trigger(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            return;
        }

        ServerPlayer epicenterPlayer = players.get(RANDOM.nextInt(players.size()));
        startShake(level, epicenterPlayer.blockPosition(), 1.0, BASE_SHAKE_TICKS);
    }

    private void startShake(ServerLevel level, BlockPos epicenter, double epicenterMagnitude, int baseDurationTicks) {
        for (ServerPlayer player : level.players()) {
            double distance = Math.sqrt(player.blockPosition().distSqr(epicenter));
            double magnitude = epicenterMagnitude * Math.max(0.0, 1.0 - distance / MAX_RADIUS);

            if (magnitude <= 0.02) {
                continue; // too far from the epicenter to feel anything
            }

            player.sendSystemMessage(Component.literal(
                    magnitude > 0.6 ? "Земля дрожит под ногами..." : "Где-то вдалеке дрожит земля..."));

            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.AMBIENT, (float) (0.4 + magnitude * 0.5), 0.5f);

            int duration = Math.max(PULSE_EVERY_TICKS, (int) (baseDurationTicks * magnitude));
            shakingPlayers.put(player.getUUID(), new ShakeState(duration, duration, magnitude));
        }
    }

    /**
     * Call once per server tick (independent of DisasterScheduler's own roll
     * interval) so shakes and aftershocks keep playing out smoothly.
     */
    public void tickActiveEffects(ServerLevel level) {
        tickShakes(level);
        tickAftershocks(level);
    }

    private void tickShakes(ServerLevel level) {
        if (shakingPlayers.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, ShakeState>> iterator = shakingPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ShakeState> entry = iterator.next();
            ShakeState shake = entry.getValue();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getKey());

            if (player == null || shake.remainingTicks <= 0) {
                if (player != null) {
                    scheduleAftershock(player.blockPosition(), shake.magnitude);
                }
                iterator.remove();
                continue;
            }

            if (shake.remainingTicks % PULSE_EVERY_TICKS == 0) {
                // real quakes hit hardest early and taper off, not constant intensity throughout
                double intensityNow = shake.magnitude * (shake.remainingTicks / (double) shake.totalTicks);
                pulse(level, player, intensityNow);
            }

            shake.remainingTicks--;
        }
    }

    private void pulse(ServerLevel level, ServerPlayer player, double intensity) {
        double shoveX = (RANDOM.nextDouble() - 0.5) * 0.5 * intensity;
        double shoveZ = (RANDOM.nextDouble() - 0.5) * 0.5 * intensity;
        player.push(shoveX, 0.05 * intensity, shoveZ);
        player.hurtMarked = true; // force the velocity change to sync client-side

        int particleCount = Math.max(2, (int) (10 * intensity));
        level.sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY() + 0.1, player.getZ(),
                particleCount, 1.0, 0.1, 1.0, 0.01);

        if (RANDOM.nextDouble() < intensity) {
            collapseNear(level, player.blockPosition(), intensity, -1); // ground below
        }
        if (RANDOM.nextDouble() < intensity * 0.6) {
            collapseNear(level, player.blockPosition(), intensity, 2); // ceiling above - cave-ins
        }
    }

    private void collapseNear(ServerLevel level, BlockPos center, double intensity, int yOffset) {
        for (int i = 0; i < CANDIDATE_POSITIONS_PER_PULSE; i++) {
            int dx = RANDOM.nextInt(COLLAPSE_RADIUS * 2 + 1) - COLLAPSE_RADIUS;
            int dz = RANDOM.nextInt(COLLAPSE_RADIUS * 2 + 1) - COLLAPSE_RADIUS;
            BlockPos pos = center.offset(dx, yOffset, dz);

            BlockState state = level.getBlockState(pos);

            if (state.isAir()
                    || !state.getFluidState().isEmpty()
                    || state.hasBlockEntity()
                    || state.getDestroySpeed(level, pos) < 0) {
                continue;
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity falling = FallingBlockEntity.fall(level, pos, state);

            if (intensity >= MIN_MAGNITUDE_TO_DAMAGE) {
                falling.hurtEntities = true;
            }
            return; // one block per collapse call - gradual, not an instant crater
        }
    }

    private void scheduleAftershock(BlockPos epicenter, double previousMagnitude) {
        if (previousMagnitude < MIN_MAGNITUDE_FOR_AFTERSHOCK) {
            return; // too weak to bother with a follow-up
        }
        int delayTicks = 100 + RANDOM.nextInt(200); // 5-15s
        double aftershockMagnitude = previousMagnitude * 0.4;
        pendingAftershocks.add(new PendingAftershock(epicenter, aftershockMagnitude, delayTicks));
    }

    private void tickAftershocks(ServerLevel level) {
        if (pendingAftershocks.isEmpty()) {
            return;
        }

        Iterator<PendingAftershock> iterator = pendingAftershocks.iterator();
        while (iterator.hasNext()) {
            PendingAftershock pending = iterator.next();
            pending.ticksRemaining--;

            if (pending.ticksRemaining <= 0) {
                startShake(level, pending.epicenter, pending.magnitude, BASE_SHAKE_TICKS / 2);
                iterator.remove();
            }
        }
    }

    private static final class ShakeState {
        int remainingTicks;
        final int totalTicks;
        final double magnitude;

        ShakeState(int remainingTicks, int totalTicks, double magnitude) {
            this.remainingTicks = remainingTicks;
            this.totalTicks = totalTicks;
            this.magnitude = magnitude;
        }
    }

    private static final class PendingAftershock {
        final BlockPos epicenter;
        final double magnitude;
        int ticksRemaining;

        PendingAftershock(BlockPos epicenter, double magnitude, int ticksRemaining) {
            this.epicenter = epicenter;
            this.magnitude = magnitude;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
