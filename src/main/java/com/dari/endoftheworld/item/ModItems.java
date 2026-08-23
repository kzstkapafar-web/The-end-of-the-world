package com.dari.endoftheworld.item;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** BlockItems for all six ModBlocks levers. See ModBlocks.java for the confirmed registration pattern. */
public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<BlockItem> GENERATOR_LEVER =
            registerBlockItem("generator_lever", ModBlocks.GENERATOR_LEVER);
    public static final RegistryObject<BlockItem> POWER_SWITCH =
            registerBlockItem("power_switch", ModBlocks.POWER_SWITCH);
    public static final RegistryObject<BlockItem> LIGHTING_SWITCH =
            registerBlockItem("lighting_switch", ModBlocks.LIGHTING_SWITCH);
    public static final RegistryObject<BlockItem> VENTILATION_VALVE =
            registerBlockItem("ventilation_valve", ModBlocks.VENTILATION_VALVE);
    public static final RegistryObject<BlockItem> WATER_PUMP =
            registerBlockItem("water_pump", ModBlocks.WATER_PUMP);
    public static final RegistryObject<BlockItem> CENTRAL_COMPUTER =
            registerBlockItem("central_computer", ModBlocks.CENTRAL_COMPUTER);

    private static RegistryObject<BlockItem> registerBlockItem(
            String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(EndOfTheWorldMod.MOD_ID, name)))
                .useBlockDescriptionPrefix()));
    }

    private ModItems() {
    }
}
