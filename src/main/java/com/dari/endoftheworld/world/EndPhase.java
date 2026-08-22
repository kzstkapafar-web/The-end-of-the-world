package com.dari.endoftheworld.world;

/**
 * The seven global phases of the catastrophe, from a normal world (0)
 * to permanent life underground in the bunker (6).
 * <p>
 * This enum is the single source of truth for phase ordering. Systems that
 * need to react to phase changes (sky rendering, disaster scheduling, bunker
 * unlock state, etc.) should read the current phase from {@link WorldEndState}
 * rather than tracking their own copy of it.
 */
public enum EndPhase {
    /** Phase 0 — normal world, no visible signs of the catastrophe yet. */
    NORMAL,
    /** Phase 1 — first signs: minor tremors, strange weather, early warnings. */
    FIRST_SIGNS,
    /** Phase 2 — instability: earthquakes and localized damage become common. */
    INSTABILITY,
    /** Phase 3 — mass disasters: meteors, volcanic activity, widespread destruction. */
    MASS_DISASTERS,
    /** Phase 4 — surface collapse: large parts of the Overworld become uninhabitable. */
    SURFACE_COLLAPSE,
    /** Phase 5 — final catastrophe: the last habitable pockets of the surface fall. */
    FINAL_CATASTROPHE,
    /** Phase 6 — underground life: the surface is gone, life continues in the bunker. Terminal phase. */
    UNDERGROUND_LIFE;

    /**
     * @return the next phase in sequence, or this same phase if already at the last one.
     */
    public EndPhase next() {
        int nextOrdinal = this.ordinal() + 1;
        EndPhase[] values = values();
        return nextOrdinal < values.length ? values[nextOrdinal] : this;
    }

    /**
     * @return true if this is the terminal phase (does not advance automatically).
     */
    public boolean isFinal() {
        return this == UNDERGROUND_LIFE;
    }
}
