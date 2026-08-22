package com.dari.endoftheworld;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Root entry point of the mod.
 * Kept intentionally empty of game logic for now — this is Stage 1 (Этап 1):
 * a minimal buildable skeleton to confirm Gradle build + game launch work
 * before any catastrophe/bunker systems are added.
 *
 * NOTE ON THIS FILE: Forge 61.x for 1.21.11 ships with the new EventBus 7
 * system (confirmed via the eventbus-validator dependency and the
 * eventbus.api.strictRuntimeChecks run property in the official 1.21.11
 * MDK build.gradle). Per EventBus 7's migration guide, the mod constructor
 * takes FMLJavaModLoadingContext and mod-bus events are obtained via
 * EventName.getBus(modBusGroup) rather than the older
 * FMLJavaModLoadingContext.get().getModEventBus() pattern.
 * I was not able to fetch the literal official ExampleMod.java for 1.21.11
 * to triple-confirm this exact signature (GitHub blocked automated access
 * to that specific file), so please run `gradlew build` first and tell me
 * the exact compiler error if this constructor doesn't match — I'll fix it
 * immediately rather than guess further.
 */
@Mod(EndOfTheWorldMod.MOD_ID)
public class EndOfTheWorldMod {

    public static final String MOD_ID = "endoftheworld";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EndOfTheWorldMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);

        LOGGER.info("[{}] Мод инициализирован (Этап 1 — фундамент)", MOD_ID);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[{}] Common setup завершён", MOD_ID);
    }
}
