package com.dari.endoftheworld.world;

import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Server-authoritative, world-wide state of the catastrophe: current phase and
 * how long it has been running. Attached to the Overworld only (the one
 * dimension that is never fully unloaded), so every dimension can read the
 * same instance via {@link #get(ServerLevel)} passing the Overworld level.
 * <p>
 * This class intentionally does nothing beyond tracking phase + time. Actual
 * disaster scheduling, sky/weather effects, and bunker unlock logic belong in
 * their own systems that read {@link #getPhase()} — see Stage 3+.
 * <p>
 * NOTE ON THIS FILE: rewritten after a second real compile failure. This
 * Minecraft version removed SavedData's overridable save()/load() methods
 * entirely — persistence is now fully codec-driven via the new SavedDataType
 * class (constructor supplier + Codec, no manual CompoundTag read/write).
 * Confirmed against the actual javadoc for 1.21.10 (closest available to our
 * 1.21.11) after both the official docs page and my first fix attempt turned
 * out stale. This is the third revision of this file's persistence code —
 * if gradlew build still fails here, please send me the exact error again.
 */
public class WorldEndState extends SavedData {

    private static final String SAVE_NAME = "endoftheworld_state";
    private static final long TICKS_PER_SECOND = 20L;

    /** Codec-driven type descriptor: id, default-instance constructor, and (de)serialization codec. */
    public static final SavedDataType<WorldEndState> TYPE = new SavedDataType<>(
            SAVE_NAME,
            WorldEndState::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("Phase").forGetter(state -> state.phase.name()),
                    Codec.LONG.fieldOf("TicksInCurrentPhase").forGetter(state -> state.ticksInCurrentPhase),
                    Codec.LONG.fieldOf("TotalTicksElapsed").forGetter(state -> state.totalTicksElapsed)
            ).apply(instance, WorldEndState::fromSavedFields))
    );

    private EndPhase phase = EndPhase.NORMAL;
    private long ticksInCurrentPhase = 0L;
    private long totalTicksElapsed = 0L;

    public WorldEndState() {
    }

    private static WorldEndState fromSavedFields(String phaseName, long ticksInCurrentPhase, long totalTicksElapsed) {
        WorldEndState state = new WorldEndState();
        state.phase = EndPhase.valueOf(phaseName);
        state.ticksInCurrentPhase = ticksInCurrentPhase;
        state.totalTicksElapsed = totalTicksElapsed;
        return state;
    }

    /**
     * @param overworld the server's Overworld level (e.g. {@code server.overworld()})
     * @return the single WorldEndState instance for this world, creating it if absent
     */
    public static WorldEndState get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(TYPE);
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
