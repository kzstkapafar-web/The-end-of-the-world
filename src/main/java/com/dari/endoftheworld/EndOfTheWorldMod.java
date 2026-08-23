package com.dari.endoftheworld;

import com.dari.endoftheworld.block.ModBlocks;
import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import com.dari.endoftheworld.event.WorldEndTickHandler;
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
 * Stage 5 (Этап 5) adds ModBlocks/ModItems registration. Least-certain part
 * of this stage: whether DeferredRegister.Blocks/.Items#register(...) takes
 * the same modBusGroup we use elsewhere, or a different bus reference —
 * registry registration wasn't covered by the EventBus 7 migration notes I
 * had for the rest of this mod. If gradlew build fails here, send the error.
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

        LOGGER.info("[{}] Мод инициализирован (Этап 5 — блок генератора)", MOD_ID);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[{}] Common setup завершён", MOD_ID);
    }
}
