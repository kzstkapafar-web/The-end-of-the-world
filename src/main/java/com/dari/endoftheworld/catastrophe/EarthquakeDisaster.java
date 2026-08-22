package com.dari.endoftheworld.catastrophe;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Stage 3's proof-of-concept disaster: no block manipulation yet, just a
 * player-facing signal (message + sound) to confirm the scheduler → disaster
 * wiring works end to end. Real shaking/block-damage effects belong in a
 * later stage once we've verified the relevant block/chunk APIs the same
 * careful way we verified SavedData and EventBus in Stage 2.
 * <p>
 * Message text is a plain literal for now — should move to a translatable
 * Component (lang file key) once client-side localization is set up.
 */
public final class EarthquakeDisaster implements Disaster {

    public static final EarthquakeDisaster INSTANCE = new EarthquakeDisaster();

    private EarthquakeDisaster() {
    }

    @Override
    public void trigger(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(Component.literal("Земля дрожит под ногами..."));
            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE,
                    SoundSource.AMBIENT, 0.6f, 0.6f);
        }
    }
}
