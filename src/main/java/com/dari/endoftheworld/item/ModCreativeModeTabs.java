package com.dari.endoftheworld.item;

import com.dari.endoftheworld.EndOfTheWorldMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * The mod's own creative-inventory tab, so items don't require /give to obtain.
 * Confirmed pattern (Forge, not NeoForge): DeferredRegister<CreativeModeTab> via
 * Registries.CREATIVE_MODE_TAB + CreativeModeTab.builder()...build(), registered
 * on the mod bus like ModBlocks/ModItems. Uses the already-defined
 * "itemGroup.endoftheworld" translation key from our lang files.
 */
public final class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EndOfTheWorldMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.endoftheworld"))
                    .icon(() -> ModItems.GENERATOR_LEVER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GENERATOR_LEVER.get());
                        output.accept(ModItems.POWER_SWITCH.get());
                        output.accept(ModItems.LIGHTING_SWITCH.get());
                        output.accept(ModItems.VENTILATION_VALVE.get());
                        output.accept(ModItems.WATER_PUMP.get());
                        output.accept(ModItems.CENTRAL_COMPUTER.get());
                    })
                    .build()
    );

    private ModCreativeModeTabs() {
    }
}
