package com.dari.endoftheworld.catastrophe;

import com.dari.endoftheworld.config.EndOfTheWorldConfig;
import com.dari.endoftheworld.world.EndPhase;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

/**
 * Server-side dispatcher that decides when to fire a disaster, based on the
 * current {@link EndPhase}. Cheap by design: only rolls dice once every
 * {@code DISASTER_CHECK_INTERVAL_SECONDS} (not every tick), never scans the
 * map, and delegates all actual work to a {@link Disaster} implementation.
 * <p>
 * Stage 3: only {@link EarthquakeDisaster} exists, so every eligible phase
 * uses it. Once more disaster types exist (meteors, volcanic activity, ash),
 * this should pick randomly from a per-phase pool instead of a single disaster.
 */
public final class DisasterScheduler {

    private static final Random RANDOM = new Random();

    private long ticksUntilNextCheck = 0L;

    /**
     * Call once per server tick (after the phase timer has been advanced).
     * Cheap when not due for a check — just a counter decrement.
     */
    public void tick(ServerLevel overworld, EndPhase phase) {
        if (ticksUntilNextCheck > 0) {
            ticksUntilNextCheck--;
            return;
        }

        ticksUntilNextCheck = EndOfTheWorldConfig.DISASTER_CHECK_INTERVAL_SECONDS.get() * 20L;

        int chancePercent = EndOfTheWorldConfig.getDisasterChancePercent(phase);
        if (chancePercent <= 0) {
            return;
        }

        if (RANDOM.nextInt(100) < chancePercent) {
            EarthquakeDisaster.INSTANCE.trigger(overworld);
        }
    }
}
