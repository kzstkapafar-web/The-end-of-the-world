package com.dari.endoftheworld;

import com.dari.endoftheworld.block.ModBlocks;
import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import com.dari.endoftheworld.event.WorldEndTickHandler;
import com.dari.endoftheworld.item.ModCreativeModeTabs;
import com.dari.endoftheworld.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Root entry point of the mod.
 *
 * Stage 1 — confirmed working: constructor signature + EventBus 7 pattern
 * (FMLJavaModLoadingContext + context.getModBusGroup() + EventName.getBus(modBusGroup)).
 *
 * Stage 2 — confirmed working: EndOfTheWorldConfig registration, WorldEndTickHandler.
 *
 * Stages 5-7 — confirmed working via real client runs, not just CI:
 * ModBlocks/ModItems registration (classic DeferredRegister<T>+RegistryObject<T>).
 *
 * Stage 8 (Этап 8) adds ModCreativeModeTabs, same registration pattern as
 * ModBlocks/ModItems (DeferredRegister<CreativeModeTab> on the mod bus).
 */
@Mod(EndOfTheWorldMod.MOD_ID)
public class EndOfTheWorldMod {

    public static final String MOD_ID = "endoftheworld";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EndOfTheWorldMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EndOfTheWorldConfig.SPEC);

        WorldEndTickHandler.register();

        ModBlocks.BLOCKS.register(modBusGroup);
        ModItems.ITEMS.register(modBusGroup);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modBusGroup);

        LOGGER.info("[{}] Мод инициализирован (Этап 8 — творческая вкладка)", MOD_ID);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[{}] Common setup завершён", MOD_ID);
    }
}
