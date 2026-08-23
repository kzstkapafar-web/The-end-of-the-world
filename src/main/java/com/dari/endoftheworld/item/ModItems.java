package com.dari.endoftheworld.item;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredItem;
import net.minecraftforge.registries.DeferredRegister;

/** See ModBlocks.java for the note on registration uncertainty — same applies here. */
public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EndOfTheWorldMod.MOD_ID);

    public static final DeferredItem<BlockItem> GENERATOR_LEVER = ITEMS.register(
            "generator_lever",
            () -> new BlockItem(ModBlocks.GENERATOR_LEVER.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
