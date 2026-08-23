package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * NOTE ON THIS FILE: two real-runtime crashes fixed here.
 * 1) "Block id not set" — confirmed: BlockBehaviour.Properties needs an
 *    explicit .setId(ResourceKey.create(Registries.BLOCK, ...)) call now.
 * 2) "cannot find symbol: ResourceLocation" — confirmed via the official
 *    1.21.10->1.21.11 migration primer: ResourceLocation was renamed to
 *    Identifier and moved from net.minecraft.resources to net.minecraft.util
 *    in exactly this version jump. Not a guess — matched the primer to our
 *    exact version.
 * Also confirmed: this Forge build uses the classic DeferredRegister<Block>
 * + RegistryObject<T> + ForgeRegistries.BLOCKS pattern, not NeoForge's
 * DeferredRegister.Blocks/DeferredBlock.
 */
public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<GeneratorLeverBlock> GENERATOR_LEVER = BLOCKS.register(
            "generator_lever",
            () -> new GeneratorLeverBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK,
                            Identifier.fromNamespaceAndPath(EndOfTheWorldMod.MOD_ID, "generator_lever")))
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(2.0f))
    );

    private ModBlocks() {
    }
}
