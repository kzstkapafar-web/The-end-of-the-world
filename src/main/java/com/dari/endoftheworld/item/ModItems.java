package com.dari.endoftheworld.item;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** See ModBlocks.java for why this uses DeferredRegister<Item> + RegistryObject, not NeoForge's DeferredItem. */
public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<BlockItem> GENERATOR_LEVER = ITEMS.register(
            "generator_lever",
            () -> new BlockItem(ModBlocks.GENERATOR_LEVER.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
