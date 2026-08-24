package com.dari.endoftheworld.catastrophe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Earthquake disaster, rebuilt as a genuinely different mechanic from a
 * generic "shove + explosion sound" effect: a seismic wave that physically
 * radiates outward from an epicenter across the terrain, like ripples on
 * water. Nothing happens to a player until the wavefront actually reaches
 * their position — this is a propagating world event, not a player-centric
 * particle burst.
 * <p>
 * Deliberately does NOT use: explosion sound, or any push/knockback on the
 * player. Instead:
 * - Sound: SoundEvents.WARDEN_HEARTBEAT — a deep, ominous, non-explosive rumble.
 * - Felt effect: MobEffects.CONFUSION (nausea/screen-warp) applied briefly to
 *   a player only at the moment the wavefront passes their position —
 *   disorientation rather than being physically shoved.
 * - Ground damage: sampled points ALONG the expanding ring itself (not
 *   randomly near the player) are converted to falling blocks as the wave
 *   passes through them, so the destruction visibly radiates outward with
 *   the wave rather than appearing as a random local cluster.
 * <p>
 * Performance: each active wave samples a small, fixed number of ring
 * positions per tick (not proportional to the ring's growing circumference),
 * and player checks are a cheap distance comparison against the small set of
 * currently-online players — no chunk/area scans.
 * <p>
 * Also spawns a {@link FaultLineSystem} fault at the epicenter — see that
 * class for the procedural canyon/sinkhole mechanic, kept in its own file
 * since it's a fully separate concern from the wave/player-effect logic here.
 * <p>
 * Epicenter now comes from {@link FaultRegistry} — a persistent set of fixed
 * geological fault points in the world, NOT a player's position. Earlier
 * revisions incorrectly used a random online player as the epicenter, which
 * made earthquakes implicitly player-following; real fault lines are fixed
 * features of the world that don't care whether anyone is nearby.
 */
public final class EarthquakeDisaster implements Disaster {

    public static final EarthquakeDisaster INSTANCE = new EarthquakeDisaster();

    private static final Random RANDOM = new Random();
    private static final double WAVE_SPEED_BLOCKS_PER_TICK = 1.0;
    private static final double MAX_RADIUS = 56.0;
    private static final double FRONT_BAND = 1.5; // how "thick" the passing wavefront is, for player detection
    private static final int TERRAIN_SAMPLES_PER_TICK = 6;
    private static final int NAUSEA_DURATION_TICKS = 100; // 5s

    private final List<SeismicWave> activeWaves = new ArrayList<>();
    private final FaultLineSystem faultLineSystem = new FaultLineSystem();

    private EarthquakeDisaster() {
    }

    @Override
    public void trigger(ServerLevel level) {
        BlockPos epicenter = FaultRegistry.get(level).pickEpicenter(level, RANDOM);

        level.playSound(null, epicenter, SoundEvents.WARDEN_HEARTBEAT,
                SoundSource.AMBIENT, 4.0f, 0.6f);

        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(Component.literal("Что-то большое движется под землёй..."));
        }

        activeWaves.add(new SeismicWave(epicenter));
        faultLineSystem.start(epicenter);
    }

    /**
     * Call once per server tick (independent of DisasterScheduler's own roll
     * interval) so waves, faults, and sinkholes keep progressing and
     * eventually dissipate on their own.
     */
    public void tickActiveEffects(ServerLevel level) {
        tickWaves(level);
        faultLineSystem.tick(level);
    }

    private void tickWaves(ServerLevel level) {
        if (activeWaves.isEmpty()) {
            return;
        }

        var iterator = activeWaves.iterator();
        while (iterator.hasNext()) {
            SeismicWave wave = iterator.next();
            wave.radius += WAVE_SPEED_BLOCKS_PER_TICK;

            if (wave.radius > MAX_RADIUS) {
                iterator.remove();
                continue;
            }

            affectPlayersOnWavefront(level, wave);
            damageTerrainAlongWavefront(level, wave);
        }
    }

    private void affectPlayersOnWavefront(ServerLevel level, SeismicWave wave) {
        for (ServerPlayer player : level.players()) {
            double distance = horizontalDistance(player.blockPosition(), wave.epicenter);

            if (Math.abs(distance - wave.radius) <= FRONT_BAND && !wave.alreadyHit.contains(player.getUUID())) {
                wave.alreadyHit.add(player.getUUID());

                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, NAUSEA_DURATION_TICKS, 0));
                level.sendParticles(ParticleTypes.CRIT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        8, 0.4, 0.4, 0.4, 0.0);
            }
        }
    }

    private void damageTerrainAlongWavefront(ServerLevel level, SeismicWave wave) {
        for (int i = 0; i < TERRAIN_SAMPLES_PER_TICK; i++) {
            double angle = RANDOM.nextDouble() * Math.PI * 2.0;
            int x = wave.epicenter.getX() + (int) Math.round(Math.cos(angle) * wave.radius);
            int z = wave.epicenter.getZ() + (int) Math.round(Math.sin(angle) * wave.radius);

            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            BlockPos pos = new BlockPos(x, surfaceY - 1, z);

            BlockState state = level.getBlockState(pos);

            if (state.isAir()
                    || !state.getFluidState().isEmpty()
                    || state.hasBlockEntity()
                    || state.getDestroySpeed(level, pos) < 0) {
                continue;
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity.fall(level, pos, state);

            level.sendParticles(ParticleTypes.CRIT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    3, 0.3, 0.1, 0.3, 0.0);
        }
    }

    private static double horizontalDistance(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static final class SeismicWave {
        final BlockPos epicenter;
        double radius = 0.0;
        final List<java.util.UUID> alreadyHit = new ArrayList<>();

        SeismicWave(BlockPos epicenter) {
            this.epicenter = epicenter;
        }
    }
}
