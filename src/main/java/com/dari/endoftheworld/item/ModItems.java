package com.dari.endoftheworld.item;

import com.dari.endoftheworld.EndOfTheWorldMod;
import com.dari.endoftheworld.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * See ModBlocks.java for both fixes applied here too: .setId(...) is required
 * on Item.Properties, and ResourceLocation was renamed to Identifier (moved
 * to net.minecraft.util) in the 1.21.10->1.21.11 jump this mod targets.
 * useBlockDescriptionPrefix() makes this BlockItem reuse the
 * block.endoftheworld.generator_lever translation key (already in our lang
 * files) instead of expecting a separate item.* key.
 */
public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<BlockItem> GENERATOR_LEVER = ITEMS.register(
            "generator_lever",
            () -> new BlockItem(ModBlocks.GENERATOR_LEVER.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM,
                            Identifier.fromNamespaceAndPath(EndOfTheWorldMod.MOD_ID, "generator_lever")))
                    .useBlockDescriptionPrefix())
    );

    private ModItems() {
    }
}
