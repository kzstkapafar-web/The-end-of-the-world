package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
 * 2) "cannot find symbol: ResourceLocation" / then "cannot find symbol: Identifier"
 *    — this took two tries. Confirmed via NeoForged's own 1.21.11 release
 *    announcement: ResourceLocation actually was renamed to Identifier in
 *    exactly this Minecraft version. My first fix wrongly also moved the
 *    import to net.minecraft.util, conflating a separate sentence in the
 *    migration primer about "most utility classes" moving there in general.
 *    Identifier stays in the original net.minecraft.resources package, just
 *    renamed in place. If this is wrong again, the fastest way to get ground
 *    truth is grepping the actual Forge/Minecraft jar in your local Gradle
 *    cache (~/.gradle/caches/...) for "class Identifier" — faster than me
 *    searching further.
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
