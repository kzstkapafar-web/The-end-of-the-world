package com.dari.endoftheworld.event;

import com.dari.endoftheworld.catastrophe.DisasterScheduler;
import com.dari.endoftheworld.world.WorldEndState;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

/**
 * Advances {@link WorldEndState} and rolls {@link DisasterScheduler} once per
 * server tick. Server-only: this class must never touch client state or run
 * on the client.
 * <p>
 * Confirmed working by a real GitHub Actions build (Stage 2): the manual
 * {@code EventName.BUS.addListener(...)} registration pattern below compiles
 * and runs correctly on this Forge build. Call {@link #register()} once from
 * the mod constructor to wire it up.
 */
public final class WorldEndTickHandler {

    private static MinecraftServer server;
    private static final DisasterScheduler DISASTER_SCHEDULER = new DisasterScheduler();

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

        WorldEndState state = WorldEndState.get(server.overworld());
        state.tick();
        DISASTER_SCHEDULER.tick(server.overworld(), state.getPhase());
    }
}
