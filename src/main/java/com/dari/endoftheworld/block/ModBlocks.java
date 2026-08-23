package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * NOTE ON THIS FILE: first version used DeferredRegister.Blocks/DeferredBlock,
 * which turned out to be NeoForge-only API — this Forge build doesn't have
 * those classes at all. Switched to the classic DeferredRegister<Block> +
 * RegistryObject<T> + ForgeRegistries.BLOCKS pattern, which is what Forge's
 * own docs actually show (I'd mistrusted that pattern earlier because other
 * Forge docs pages turned out stale, but this one was right all along).
 */
public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<GeneratorLeverBlock> GENERATOR_LEVER = BLOCKS.register(
            "generator_lever",
            () -> new GeneratorLeverBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(2.0f))
    );

    private ModBlocks() {
    }
}
