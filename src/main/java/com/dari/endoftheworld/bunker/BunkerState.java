package com.dari.endoftheworld.bunker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Server-authoritative state of the bunker's restoration progress: which
 * {@link BunkerSystem}s have been brought back online. One instance per
 * world, attached to the Overworld — same pattern as WorldEndState.
 * <p>
 * Uses the same codec-based SavedDataType approach already confirmed
 * working for WorldEndState in Stage 2 (including the 4-arg constructor
 * with an explicit null DataFixTypes — this Forge build has no 3-arg
 * convenience overload).
 */
public class BunkerState extends SavedData {

    private static final String SAVE_NAME = "endoftheworld_bunker_state";

    private static final Codec<BunkerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("RestoredSystems").forGetter(BunkerState::restoredSystemNames)
    ).apply(instance, BunkerState::fromSavedFields));

    public static final SavedDataType<BunkerState> TYPE = new SavedDataType<>(SAVE_NAME, BunkerState::new, CODEC, null);

    private final Set<BunkerSystem> restoredSystems = EnumSet.noneOf(BunkerSystem.class);

    public BunkerState() {
    }

    private List<String> restoredSystemNames() {
        return restoredSystems.stream().map(Enum::name).toList();
    }

    private static BunkerState fromSavedFields(List<String> restoredSystemNames) {
        BunkerState state = new BunkerState();
        for (String name : restoredSystemNames) {
            state.restoredSystems.add(BunkerSystem.valueOf(name));
        }
        return state;
    }

    /**
     * @param overworld the server's Overworld level (e.g. {@code server.overworld()})
     * @return the single BunkerState instance for this world, creating it if absent
     */
    public static BunkerState get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean isRestored(BunkerSystem system) {
        return restoredSystems.contains(system);
    }

    /**
     * Attempts to restore the given system.
     *
     * @return true if restored successfully, false if its prerequisite isn't restored yet
     * or it was already restored.
     */
    public boolean restore(BunkerSystem system) {
        if (restoredSystems.contains(system)) {
            return false;
        }

        BunkerSystem prerequisite = system.prerequisite();
        if (prerequisite != null && !restoredSystems.contains(prerequisite)) {
            return false;
        }

        restoredSystems.add(system);
        setDirty();
        return true;
    }
}
