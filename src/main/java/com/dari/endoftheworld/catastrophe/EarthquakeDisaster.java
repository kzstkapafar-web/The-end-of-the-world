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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Earthquake disaster, spread over time instead of one instant frame.
 * trigger() starts a ~1.5s "shake" per player; tickActiveEffects() (called
 * every server tick from WorldEndTickHandler, not just on the disaster-roll
 * interval) applies a push+particles+ground-collapse pulse every few ticks
 * for the duration, so it reads as real shaking rather than a single poof.
 * <p>
 * Fix from the previous revision: a single Player#push(...) on the server is
 * often invisible client-side, because player movement is client-driven and
 * the client's own input silently overrides a tiny one-off server velocity
 * change. Setting player.hurtMarked = true (the same flag vanilla knockback
 * code uses) forces that velocity change to actually sync to the client.
 * hurtMarked is a long-standing vanilla Entity field, not something new to
 * this version, but — like everything else here — not yet confirmed against
 * an actual compile for this exact build.
 * <p>
 * Ground collapse: FallingBlockEntity.fall(Level, BlockPos, BlockState),
 * same safety filters as before (skip air/liquid/block-entities/unbreakable),
 * but now one block per pulse instead of several at once, so the ground
 * visibly cracks open over time rather than an instant crater.
 */
public final class EarthquakeDisaster implements Disaster {

    public static final EarthquakeDisaster INSTANCE = new EarthquakeDisaster();

    private static final Random RANDOM = new Random();
    private static final int SHAKE_DURATION_TICKS = 30; // ~1.5s at 20 ticks/sec
    private static final int PULSE_EVERY_TICKS = 4;
    private static final int CANDIDATE_POSITIONS_PER_PULSE = 6;
    private static final int RADIUS = 3;

    /** Players currently mid-earthquake, mapped to ticks remaining. Server-thread only. */
    private final Map<UUID, Integer> shakingPlayers = new HashMap<>();

    private EarthquakeDisaster() {
    }

    @Override
    public void trigger(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(Component.literal("Земля дрожит под ногами..."));
            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.AMBIENT, 0.8f, 0.5f);
            shakingPlayers.put(player.getUUID(), SHAKE_DURATION_TICKS);
        }
    }

    /**
     * Call once per server tick (independent of DisasterScheduler's own roll
     * interval) so an in-progress earthquake keeps playing out smoothly.
     * Cheap when no earthquake is active — returns immediately.
     */
    public void tickActiveEffects(ServerLevel level) {
        if (shakingPlayers.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, Integer>> iterator = shakingPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getKey());

            if (player == null || entry.getValue() <= 0) {
                iterator.remove();
                continue;
            }

            int remaining = entry.getValue();

            if (remaining % PULSE_EVERY_TICKS == 0) {
                pulse(level, player);
            }

            entry.setValue(remaining - 1);
        }
    }

    private void pulse(ServerLevel level, ServerPlayer player) {
        double shoveX = (RANDOM.nextDouble() - 0.5) * 0.5;
        double shoveZ = (RANDOM.nextDouble() - 0.5) * 0.5;
        player.push(shoveX, 0.05, shoveZ);
        player.hurtMarked = true; // force the velocity change to sync client-side

        level.sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY() + 0.1, player.getZ(),
                10, 1.0, 0.1, 1.0, 0.01);

        collapseOneBlockNear(level, player.blockPosition());
    }

    private void collapseOneBlockNear(ServerLevel level, BlockPos center) {
        for (int i = 0; i < CANDIDATE_POSITIONS_PER_PULSE; i++) {
            int dx = RANDOM.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dz = RANDOM.nextInt(RADIUS * 2 + 1) - RADIUS;
            BlockPos pos = center.offset(dx, -1, dz);

            BlockState state = level.getBlockState(pos);

            if (state.isAir()
                    || !state.getFluidState().isEmpty()
                    || state.hasBlockEntity()
                    || state.getDestroySpeed(level, pos) < 0) {
                continue;
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity.fall(level, pos, state);
            return; // one block per pulse - gradual, not an instant crater
        }
    }
}
