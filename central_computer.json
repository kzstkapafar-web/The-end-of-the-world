package com.dari.endoftheworld.block;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.bunker.BunkerSystem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All six bunker-system trigger blocks, one per BunkerSystem. Each is a
 * BunkerSystemLeverBlock registered under its own block id; see that class
 * for the shared right-click -> BunkerManager.tryRestore logic.
 * <p>
 * Confirmed working (Stage 5, generator_lever): classic
 * DeferredRegister<Block> + RegistryObject<T> + ForgeRegistries.BLOCKS
 * (not NeoForge's DeferredRegister.Blocks/DeferredBlock), and
 * BlockBehaviour.Properties.setId(ResourceKey.create(Registries.BLOCK,
 * Identifier.fromNamespaceAndPath(...))) is required or the game crashes
 * at runtime with "Block id not set" (Identifier lives in
 * net.minecraft.resources in this build, not net.minecraft.util).
 */
public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<BunkerSystemLeverBlock> GENERATOR_LEVER =
            registerLever("generator_lever", BunkerSystem.GENERATOR);
    public static final RegistryObject<BunkerSystemLeverBlock> POWER_SWITCH =
            registerLever("power_switch", BunkerSystem.POWER);
    public static final RegistryObject<BunkerSystemLeverBlock> LIGHTING_SWITCH =
            registerLever("lighting_switch", BunkerSystem.LIGHTING);
    public static final RegistryObject<BunkerSystemLeverBlock> VENTILATION_VALVE =
            registerLever("ventilation_valve", BunkerSystem.VENTILATION);
    public static final RegistryObject<BunkerSystemLeverBlock> WATER_PUMP =
            registerLever("water_pump", BunkerSystem.WATER);
    public static final RegistryObject<BunkerSystemLeverBlock> CENTRAL_COMPUTER =
            registerLever("central_computer", BunkerSystem.CENTRAL_COMPUTER);

    private static RegistryObject<BunkerSystemLeverBlock> registerLever(String name, BunkerSystem system) {
        return BLOCKS.register(name, () -> new BunkerSystemLeverBlock(
                BlockBehaviour.Properties.of()
                        .setId(ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(EndOfTheWorldMod.MOD_ID, name)))
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .strength(2.0f),
                system));
    }

    private ModBlocks() {
    }
}
