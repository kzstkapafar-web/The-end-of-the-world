package com.dari.endoftheworld.config;

import com.dari.endoftheworld.world.EndPhase;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common config: how long (in real-time seconds) the world stays in each
 * phase before advancing to the next one. Everything here is server-authoritative
 * and world-agnostic — no client-only settings belong in this file.
 */
public final class EndOfTheWorldConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue PHASE_NORMAL_DURATION_SECONDS;
    public static final ForgeConfigSpec.IntValue PHASE_FIRST_SIGNS_DURATION_SECONDS;
    public static final ForgeConfigSpec.IntValue PHASE_INSTABILITY_DURATION_SECONDS;
    public static final ForgeConfigSpec.IntValue PHASE_MASS_DISASTERS_DURATION_SECONDS;
    public static final ForgeConfigSpec.IntValue PHASE_SURFACE_COLLAPSE_DURATION_SECONDS;
    public static final ForgeConfigSpec.IntValue PHASE_FINAL_CATASTROPHE_DURATION_SECONDS;

    public static final ForgeConfigSpec.IntValue DISASTER_CHECK_INTERVAL_SECONDS;
    public static final ForgeConfigSpec.IntValue FIRST_SIGNS_DISASTER_CHANCE_PERCENT;
    public static final ForgeConfigSpec.IntValue INSTABILITY_DISASTER_CHANCE_PERCENT;
    public static final ForgeConfigSpec.IntValue MASS_DISASTERS_DISASTER_CHANCE_PERCENT;
    public static final ForgeConfigSpec.IntValue SURFACE_COLLAPSE_DISASTER_CHANCE_PERCENT;
    public static final ForgeConfigSpec.IntValue FINAL_CATASTROPHE_DISASTER_CHANCE_PERCENT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "Duration of each phase in real-time seconds before the world advances to the next phase.",
                "Phase UNDERGROUND_LIFE (the last one) has no duration here — it never advances automatically."
        ).push("phases");

        PHASE_NORMAL_DURATION_SECONDS = builder
                .comment("Phase 0 — NORMAL: how long before FIRST_SIGNS begins.")
                .defineInRange("phase0_normal_duration_seconds", 1800, 0, Integer.MAX_VALUE);

        PHASE_FIRST_SIGNS_DURATION_SECONDS = builder
                .comment("Phase 1 — FIRST_SIGNS: how long before INSTABILITY begins.")
                .defineInRange("phase1_first_signs_duration_seconds", 1800, 0, Integer.MAX_VALUE);

        PHASE_INSTABILITY_DURATION_SECONDS = builder
                .comment("Phase 2 — INSTABILITY: how long before MASS_DISASTERS begins.")
                .defineInRange("phase2_instability_duration_seconds", 1800, 0, Integer.MAX_VALUE);

        PHASE_MASS_DISASTERS_DURATION_SECONDS = builder
                .comment("Phase 3 — MASS_DISASTERS: how long before SURFACE_COLLAPSE begins.")
                .defineInRange("phase3_mass_disasters_duration_seconds", 1800, 0, Integer.MAX_VALUE);

        PHASE_SURFACE_COLLAPSE_DURATION_SECONDS = builder
                .comment("Phase 4 — SURFACE_COLLAPSE: how long before FINAL_CATASTROPHE begins.")
                .defineInRange("phase4_surface_collapse_duration_seconds", 1800, 0, Integer.MAX_VALUE);

        PHASE_FINAL_CATASTROPHE_DURATION_SECONDS = builder
                .comment("Phase 5 — FINAL_CATASTROPHE: how long before UNDERGROUND_LIFE (terminal) begins.")
                .defineInRange("phase5_final_catastrophe_duration_seconds", 900, 0, Integer.MAX_VALUE);

        builder.pop();

        builder.comment(
                "Disaster scheduling: how often the game rolls the dice for a disaster, and how likely",
                "a disaster is to fire on each roll, per phase. Phases NORMAL and UNDERGROUND_LIFE never",
                "roll for disasters and are not configurable here."
        ).push("disasters");

        DISASTER_CHECK_INTERVAL_SECONDS = builder
                .comment("How often (real-time seconds) the scheduler rolls for a disaster.")
                .defineInRange("disaster_check_interval_seconds", 30, 1, Integer.MAX_VALUE);

        FIRST_SIGNS_DISASTER_CHANCE_PERCENT = builder
                .comment("Chance (0-100) that phase 1 — FIRST_SIGNS — fires a disaster on each roll.")
                .defineInRange("phase1_first_signs_disaster_chance_percent", 10, 0, 100);

        INSTABILITY_DISASTER_CHANCE_PERCENT = builder
                .comment("Chance (0-100) that phase 2 — INSTABILITY — fires a disaster on each roll.")
                .defineInRange("phase2_instability_disaster_chance_percent", 25, 0, 100);

        MASS_DISASTERS_DISASTER_CHANCE_PERCENT = builder
                .comment("Chance (0-100) that phase 3 — MASS_DISASTERS — fires a disaster on each roll.")
                .defineInRange("phase3_mass_disasters_disaster_chance_percent", 50, 0, 100);

        SURFACE_COLLAPSE_DISASTER_CHANCE_PERCENT = builder
                .comment("Chance (0-100) that phase 4 — SURFACE_COLLAPSE — fires a disaster on each roll.")
                .defineInRange("phase4_surface_collapse_disaster_chance_percent", 70, 0, 100);

        FINAL_CATASTROPHE_DISASTER_CHANCE_PERCENT = builder
                .comment("Chance (0-100) that phase 5 — FINAL_CATASTROPHE — fires a disaster on each roll.")
                .defineInRange("phase5_final_catastrophe_disaster_chance_percent", 90, 0, 100);

        builder.pop();

        SPEC = builder.build();
    }

    private EndOfTheWorldConfig() {
    }

    /**
     * @return configured duration in seconds for the given phase, or -1 if the
     * phase never advances automatically (currently only UNDERGROUND_LIFE).
     */
    public static int getDurationSeconds(EndPhase phase) {
        return switch (phase) {
            case NORMAL -> PHASE_NORMAL_DURATION_SECONDS.get();
            case FIRST_SIGNS -> PHASE_FIRST_SIGNS_DURATION_SECONDS.get();
            case INSTABILITY -> PHASE_INSTABILITY_DURATION_SECONDS.get();
            case MASS_DISASTERS -> PHASE_MASS_DISASTERS_DURATION_SECONDS.get();
            case SURFACE_COLLAPSE -> PHASE_SURFACE_COLLAPSE_DURATION_SECONDS.get();
            case FINAL_CATASTROPHE -> PHASE_FINAL_CATASTROPHE_DURATION_SECONDS.get();
            case UNDERGROUND_LIFE -> -1;
        };
    }

    /**
     * @return chance (0-100) of a disaster firing on each scheduler roll during the given phase,
     * or 0 for phases that never roll (NORMAL, UNDERGROUND_LIFE).
     */
    public static int getDisasterChancePercent(EndPhase phase) {
        return switch (phase) {
            case NORMAL, UNDERGROUND_LIFE -> 0;
            case FIRST_SIGNS -> FIRST_SIGNS_DISASTER_CHANCE_PERCENT.get();
            case INSTABILITY -> INSTABILITY_DISASTER_CHANCE_PERCENT.get();
            case MASS_DISASTERS -> MASS_DISASTERS_DISASTER_CHANCE_PERCENT.get();
            case SURFACE_COLLAPSE -> SURFACE_COLLAPSE_DISASTER_CHANCE_PERCENT.get();
            case FINAL_CATASTROPHE -> FINAL_CATASTROPHE_DISASTER_CHANCE_PERCENT.get();
        };
    }
}
