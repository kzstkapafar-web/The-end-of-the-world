package com.dari.endoftheworld.bunker;

/**
 * The bunker's internal systems, in the order they must be restored.
 * Per the mod's design doc: generator -> power -> lighting -> ventilation ->
 * water -> central computer. {@link #prerequisite()} enforces that order —
 * you can't restore POWER before GENERATOR, etc. Order is simply "the
 * previous enum constant", computed by ordinal rather than stored per
 * constant (Java enums can't forward-reference sibling constants in their
 * own constructor arguments).
 */
public enum BunkerSystem {
    GENERATOR,
    POWER,
    LIGHTING,
    VENTILATION,
    WATER,
    CENTRAL_COMPUTER;

    /**
     * @return the system that must be restored before this one, or null for GENERATOR (the first).
     */
    public BunkerSystem prerequisite() {
        int previousOrdinal = this.ordinal() - 1;
        return previousOrdinal >= 0 ? values()[previousOrdinal] : null;
    }
}
