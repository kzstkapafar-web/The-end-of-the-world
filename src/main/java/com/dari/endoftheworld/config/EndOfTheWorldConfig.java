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
}
