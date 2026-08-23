package com.dari.endoftheworld.catastrophe;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Random;

/**
 * Earthquake disaster. Stage 3 shipped this as a message+sound-only proof
 * of concept; this revision adds a real, felt effect without touching block
 * state (no chunk scans, no block manipulation — that's a separate, higher-risk
 * task for later): a dust/debris particle burst around each player plus a
 * small random horizontal shove to simulate losing footing.
 * <p>
 * sendParticles(ParticleOptions, x, y, z, count, xOffset, yOffset, zOffset, speed)
 * and Entity#push(x, y, z) are both long-standing, version-stable vanilla APIs —
 * lower risk than most of what we've verified in this mod so far, but still
 * unconfirmed against an actual compile for this exact build. Send the error
 * if gradlew build disagrees.
 * <p>
 * Message text is a plain literal for now — should move to a translatable
 * Component (lang file key) once client-side localization is set up.
 */
public final class EarthquakeDisaster implements Disaster {

    public static final EarthquakeDisaster INSTANCE = new EarthquakeDisaster();

    private static final Random RANDOM = new Random();

    private EarthquakeDisaster() {
    }

    @Override
    public void trigger(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(Component.literal("Земля дрожит под ногами..."));

            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.AMBIENT, 0.6f, 0.6f);

            level.sendParticles(ParticleTypes.EXPLOSION,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    6, 0.6, 0.05, 0.6, 0.0);

            double shoveX = (RANDOM.nextDouble() - 0.5) * 0.4;
            double shoveZ = (RANDOM.nextDouble() - 0.5) * 0.4;
            player.push(shoveX, 0.0, shoveZ);
        }
    }
}
