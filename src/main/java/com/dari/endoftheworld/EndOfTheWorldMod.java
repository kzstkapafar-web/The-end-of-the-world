package com.dari.endoftheworld;

import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import com.dari.endoftheworld.event.WorldEndTickHandler;
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
 * Stage 1 (Этап 1) — confirmed via a real GitHub Actions build: the
 * constructor signature and EventBus 7 pattern below (FMLJavaModLoadingContext
 * + context.getModBusGroup() + EventName.getBus(modBusGroup)) compile and
 * run correctly against Forge 61.1.5 / Minecraft 1.21.11.
 *
 * Stage 2 (Этап 2) adds registration of EndOfTheWorldConfig and wires up
 * WorldEndTickHandler on the game bus. ModLoadingContext.get().registerConfig(...)
 * compiled fine but produces a [removal] deprecation warning (non-blocking) —
 * left as-is for now since it doesn't fail the build; can be cleaned up later
 * in favour of a direct container reference.
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

        LOGGER.info("[{}] Мод инициализирован (Этап 2 — фаза/таймер/сохранение)", MOD_ID);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[{}] Common setup завершён", MOD_ID);
    }
}
