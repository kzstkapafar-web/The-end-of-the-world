package com.dari.endoftheworld.world;

import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Server-authoritative, world-wide state of the catastrophe: current phase and
 * how long it has been running. Attached to the Overworld only (the one
 * dimension that is never fully unloaded — see Forge's Saved Data docs), so
 * every dimension can read the same instance via {@link #get(ServerLevel)}
 * passing the Overworld level.
 * <p>
 * This class intentionally does nothing beyond tracking phase + time. Actual
 * disaster scheduling, sky/weather effects, and bunker unlock logic belong in
 * their own systems that read {@link #getPhase()} — see Stage 3+.
 */
public class WorldEndState extends SavedData {

    private static final String SAVE_NAME = "endoftheworld_state";
    private static final long TICKS_PER_SECOND = 20L;

    private EndPhase phase = EndPhase.NORMAL;
    private long ticksInCurrentPhase = 0L;
    private long totalTicksElapsed = 0L;

    /**
     * @param overworld the server's Overworld level (e.g. {@code server.overworld()})
     * @return the single WorldEndState instance for this world, creating it if absent
     */
    public static WorldEndState get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(WorldEndState::load, WorldEndState::create, SAVE_NAME);
    }

    public static WorldEndState create() {
        return new WorldEndState();
    }

    public static WorldEndState load(CompoundTag tag) {
        WorldEndState state = create();
        state.phase = EndPhase.valueOf(tag.getString("Phase"));
        state.ticksInCurrentPhase = tag.getLong("TicksInCurrentPhase");
        state.totalTicksElapsed = tag.getLong("TotalTicksElapsed");
        return state;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("Phase", phase.name());
        tag.putLong("TicksInCurrentPhase", ticksInCurrentPhase);
        tag.putLong("TotalTicksElapsed", totalTicksElapsed);
        return tag;
    }

    public EndPhase getPhase() {
        return phase;
    }

    public long getTicksInCurrentPhase() {
        return ticksInCurrentPhase;
    }

    public long getTotalTicksElapsed() {
        return totalTicksElapsed;
    }

    /**
     * Advances the phase timer by one tick. Cheap by design (a few field
     * increments and a comparison) — safe to call every server tick.
     * Called from WorldEndTickHandler; do not call from client code.
     */
    public void tick() {
        totalTicksElapsed++;

        if (phase.isFinal()) {
            setDirty();
            return;
        }

        ticksInCurrentPhase++;

        long durationTicks = EndOfTheWorldConfig.getDurationSeconds(phase) * TICKS_PER_SECOND;
        if (durationTicks > 0 && ticksInCurrentPhase >= durationTicks) {
            advancePhase();
        }

        setDirty();
    }

    private void advancePhase() {
        phase = phase.next();
        ticksInCurrentPhase = 0L;
        setDirty();
    }
}
