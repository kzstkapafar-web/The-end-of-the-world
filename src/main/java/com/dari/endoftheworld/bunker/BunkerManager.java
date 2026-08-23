package com.dari.endoftheworld.bunker;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Thin, player-facing entry point for restoring bunker systems. Later stages
 * (e.g. a generator lever block, a control panel item) should call
 * {@link #tryRestore(ServerLevel, ServerPlayer, BunkerSystem)} rather than
 * touching {@link BunkerState} directly, so all restoration feedback stays
 * consistent in one place.
 * <p>
 * Message text is a plain literal for now, same as EarthquakeDisaster — move
 * to translatable Components once client-side localization is set up.
 */
public final class BunkerManager {

    private BunkerManager() {
    }

    /**
     * Attempts to restore a bunker system on behalf of a player, sending them
     * feedback either way.
     *
     * @return true if the system was restored, false if it couldn't be (already
     * restored, or its prerequisite isn't restored yet).
     */
    public static boolean tryRestore(ServerLevel overworld, ServerPlayer player, BunkerSystem system) {
        BunkerState state = BunkerState.get(overworld);

        if (state.isRestored(system)) {
            player.sendSystemMessage(Component.literal("Эта система уже восстановлена."));
            return false;
        }

        BunkerSystem prerequisite = system.prerequisite();
        if (prerequisite != null && !state.isRestored(prerequisite)) {
            player.sendSystemMessage(Component.literal(
                    "Сначала нужно восстановить: " + prerequisite.name()));
            return false;
        }

        state.restore(system);
        player.sendSystemMessage(Component.literal("Система восстановлена: " + system.name()));
        return true;
    }
}
