package com.dari.endoftheworld.catastrophe;

import net.minecraft.server.level.ServerLevel;

/**
 * A single disaster effect that can be triggered on the Overworld. Implementations
 * must be server-only and stay lightweight (see DisasterScheduler for the budget
 * this runs under) — no full-chunk scans, no expensive per-block work without caching.
 */
@FunctionalInterface
public interface Disaster {
    void trigger(ServerLevel level);
}
