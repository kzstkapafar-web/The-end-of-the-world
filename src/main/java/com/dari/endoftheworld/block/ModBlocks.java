package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredBlock;
import net.minecraftforge.registries.DeferredRegister;

/**
 * NOTE ON THIS FILE: registration is the least-certain part of Stage 5.
 * DeferredRegister.Blocks / DeferredBlock / createBlocks(...) is the current
 * (post-RegistryObject) naming across both Forge and NeoForge documentation
 * for 1.21.x, but I don't have direct confirmation for this exact Forge
 * build, and it's not yet clear whether BLOCKS.register(...) in
 * EndOfTheWorldMod's constructor should take context.getModBusGroup() (the
 * BusGroup type already confirmed elsewhere in this mod) or something else —
 * registry registration wasn't covered by the EventBus 7 migration guide I
 * used for the rest of the event-bus work. If gradlew build fails here,
 * send me the exact error.
 */
public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EndOfTheWorldMod.MOD_ID);

    public static final DeferredBlock<GeneratorLeverBlock> GENERATOR_LEVER = BLOCKS.register(
            "generator_lever",
            () -> new GeneratorLeverBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(2.0f))
    );

    private ModBlocks() {
    }
}
