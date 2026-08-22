package com.dari.endoftheworld;

import com.dari.endoftheworld.config.EndOfTheWorldConfig;
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
 * Stage 2 (Этап 2) adds registration of EndOfTheWorldConfig — the common
 * config spec that drives phase durations for WorldEndState.
 * ModLoadingContext.get().registerConfig(...) is the long-standing,
 * multi-version-stable pattern for config registration; unlike the
 * constructor signature above, this one has not been verified against an
 * actual compile yet — if gradlew build fails here, send me the exact error.
 */
@Mod(EndOfTheWorldMod.MOD_ID)
public class EndOfTheWorldMod {

    public static final String MOD_ID = "endoftheworld";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EndOfTheWorldMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EndOfTheWorldConfig.SPEC);

        LOGGER.info("[{}] Мод инициализирован (Этап 2 — фаза/таймер/сохранение)", MOD_ID);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[{}] Common setup завершён", MOD_ID);
    }
}
