package com.dari.endoftheworld.catastrophe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Persistent registry of the world's fault-line epicenters — fixed
 * geological points that earthquakes originate from, generated once (near
 * spawn, on first use) and reused for every subsequent quake. Mirrors how
 * real fault lines are fixed geological features that rupture repeatedly
 * over time, rather than treating each earthquake as a one-off event with
 * no location memory.
 * <p>
 * Deliberately NOT tied to any player: an epicenter is a place in the world,
 * not a person. Earthquakes happen at their fault regardless of whether
 * anyone is nearby to feel them — this is the fix for the previous version,
 * which incorrectly used a random online player's position as the epicenter.
 * <p>
 * Same codec-based SavedData pattern already confirmed working for
 * WorldEndState and BunkerState, including the 4-arg SavedDataType
 * constructor this Forge build requires (no 3-arg convenience overload).
 */
public class FaultRegistry extends SavedData {

    private static final String SAVE_NAME = "endoftheworld_fault_registry";
    private static final int TARGET_FAULT_COUNT = 5;
    private static final int SPAWN_SEARCH_RADIUS = 400;

    private static final Codec<FaultRegistry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.listOf().fieldOf("Epicenters").forGetter(registry -> registry.epicenters)
    ).apply(instance, FaultRegistry::fromSavedFields));

    public static final SavedDataType<FaultRegistry> TYPE =
            new SavedDataType<>(SAVE_NAME, FaultRegistry::new, CODEC, null);

    private final List<BlockPos> epicenters = new ArrayList<>();

    public FaultRegistry() {
    }

    private static FaultRegistry fromSavedFields(List<BlockPos> epicenters) {
        FaultRegistry registry = new FaultRegistry();
        registry.epicenters.addAll(epicenters);
        return registry;
    }

    public static FaultRegistry get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }

    /**
     * @return a random existing fault epicenter, generating the initial set
     * of faults near spawn on first use if none exist yet.
     */
    public BlockPos pickEpicenter(ServerLevel level, Random random) {
        if (epicenters.isEmpty()) {
            generateInitialFaults(level, random);
        }
        return epicenters.get(random.nextInt(epicenters.size()));
    }

    private void generateInitialFaults(ServerLevel level, Random random) {
        BlockPos spawn = level.getSharedSpawnPos();

        for (int i = 0; i < TARGET_FAULT_COUNT; i++) {
            int dx = random.nextInt(SPAWN_SEARCH_RADIUS * 2) - SPAWN_SEARCH_RADIUS;
            int dz = random.nextInt(SPAWN_SEARCH_RADIUS * 2) - SPAWN_SEARCH_RADIUS;
            int x = spawn.getX() + dx;
            int z = spawn.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            epicenters.add(new BlockPos(x, y, z));
        }

        setDirty();
    }
}
