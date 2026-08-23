package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * NOTE ON THIS FILE: second real-runtime crash fixed here — "Block id not set".
 * Confirmed cause (not guessed): since a recent Minecraft version, BlockBehaviour.Properties
 * requires an explicit .setId(ResourceKey.create(Registries.BLOCK, ...)) call before being
 * passed into a Block constructor; the registry no longer assigns the name automatically
 * after the fact. Added below. Also confirmed: this Forge build uses the classic
 * DeferredRegister<Block> + RegistryObject<T> + ForgeRegistries.BLOCKS pattern, not
 * NeoForge's DeferredRegister.Blocks/DeferredBlock.
 */
public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<GeneratorLeverBlock> GENERATOR_LEVER = BLOCKS.register(
            "generator_lever",
            () -> new GeneratorLeverBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath(EndOfTheWorldMod.MOD_ID, "generator_lever")))
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(2.0f))
    );

    private ModBlocks() {
    }
}
