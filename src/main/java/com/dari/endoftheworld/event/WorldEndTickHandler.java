package com.dari.endoftheworld.event;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.world.WorldEndState;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Advances {@link WorldEndState} once per server tick. Server-only: this class
 * must never touch client state or run on the client.
 * <p>
 * NOTE ON THIS FILE: uses the classic {@code @Mod.EventBusSubscriber} +
 * {@code @SubscribeEvent} annotation style rather than the new manual
 * BUS-field style we used in EndOfTheWorldMod.java. Per EventBus 7's
 * migration guide, the annotation-driven style is the one kept
 * backward-compatible on purpose, so this should need no changes — but I
 * could not verify it against a real compile the way we did for Stage 1.
 * If gradlew build fails here, send me the exact error and I'll fix it.
 */
@Mod.EventBusSubscriber(modid = EndOfTheWorldMod.MOD_ID)
public final class WorldEndTickHandler {

    private static MinecraftServer server;

    private WorldEndTickHandler() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        server = null;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (server == null) {
            return;
        }

        WorldEndState.get(server.overworld()).tick();
    }
}
