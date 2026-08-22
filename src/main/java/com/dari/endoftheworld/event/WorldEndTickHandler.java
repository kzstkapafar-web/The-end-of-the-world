package com.dari.endoftheworld.event;

import com.dari.endoftheworld.world.WorldEndState;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

/**
 * Advances {@link WorldEndState} once per server tick. Server-only: this class
 * must never touch client state or run on the client.
 * <p>
 * NOTE ON THIS FILE: originally written with {@code @Mod.EventBusSubscriber} +
 * {@code @SubscribeEvent}, which turned out not to compile — the annotation's
 * package (net.minecraftforge.eventbus.api.SubscribeEvent) does not exist in
 * EventBus 7. Per the EventBus 7 migration guide, the replacement pattern is
 * "MinecraftForge.EVENT_BUS -> EventName.BUS", i.e. every Forge event class
 * now exposes a static BUS field to add listeners to directly — the same
 * pattern already confirmed working for FMLCommonSetupEvent in
 * EndOfTheWorldMod.java. Call {@link #register()} once from that class's
 * constructor to wire these listeners up.
 * This is my best-grounded guess given the migration guide, but — like the
 * rest of this stage — it has not been confirmed by an actual compile yet.
 * If gradlew build still fails here, send me the exact error.
 */
public final class WorldEndTickHandler {

    private static MinecraftServer server;

    private WorldEndTickHandler() {
    }

    /** Registers this handler's listeners on the game (Forge) event bus. Call once from the mod constructor. */
    public static void register() {
        ServerStartingEvent.BUS.addListener(WorldEndTickHandler::onServerStarting);
        ServerStoppingEvent.BUS.addListener(WorldEndTickHandler::onServerStopping);
        TickEvent.ServerTickEvent.Pre.BUS.addListener(WorldEndTickHandler::onServerTick);
    }

    private static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        server = null;
    }

    private static void onServerTick(TickEvent.ServerTickEvent.Pre event) {
        if (server == null) {
            return;
        }
        WorldEndState.get(server.overworld()).tick();
    }
}
