package com.dari.endoftheworld.catastrophe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.List;

/**
 * Unified "insufficient support" physics check — a single mechanic covering
 * rock overhangs, player-built structures (roofs, bridges, walls without a
 * foundation), and trees, rather than three separate hand-coded systems.
 * Vanilla Minecraft has no concept of structural support at all outside
 * sand/gravel/anvils falling straight down when the block directly below is
 * air; this checks a wider support neighborhood (below AND to the sides)
 * so overhangs and unsupported spans actually give way, the way real
 * structures do during an earthquake.
 * <p>
 * Trees get special handling: a detected log column doesn't just vanish or
 * drop straight down like a generic block — it topples sideways, one log at
 * a time from the bottom up with a short delay between each, so it visibly
 * falls over rather than disappearing instantly.
 * <p>
 * Performance: called with a small set of candidate positions per tick
 * (from the seismic wavefront in EarthquakeDisaster) — this class does not
 * itself scan chunks or areas.
 */
public final class StructuralCollapseSystem {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final int SUPPORT_CHECK_RADIUS = 2;
    private static final int MAX_TREE_HEIGHT = 24;
    private static final List<TogglingTree> togglingTrees = new ArrayList<>();

    private StructuralCollapseSystem() {
    }

    /**
     * Checks a single position: if it's a log, starts toppling the tree it
     * belongs to; otherwise, if the block there lacks support, lets it fall
     * like a generic collapsing block.
     */
    public static void checkAndCollapse(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) {
            return;
        }

        if (state.is(BlockTags.LOGS)) {
            startTreeToppling(level, pos);
            return;
        }

        if (state.getDestroySpeed(level, pos) < 0) {
            return; // unbreakable, e.g. bedrock
        }

        if (!hasSupport(level, pos)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity.fall(level, pos, state);
        }
    }

    /**
     * @return true if the block below OR at least one block in the
     * surrounding horizontal radius (at the same or one-lower level) is
     * solid — a simplified stand-in for real structural support.
     */
    private static boolean hasSupport(ServerLevel level, BlockPos pos) {
        if (isSolid(level, pos.below())) {
            return true;
        }

        for (int dx = -SUPPORT_CHECK_RADIUS; dx <= SUPPORT_CHECK_RADIUS; dx++) {
            for (int dz = -SUPPORT_CHECK_RADIUS; dz <= SUPPORT_CHECK_RADIUS; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (isSolid(level, pos.offset(dx, 0, dz)) || isSolid(level, pos.offset(dx, -1, dz))) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isSolid(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getFluidState().isEmpty();
    }

    private static void startTreeToppling(ServerLevel level, BlockPos base) {
        List<BlockPos> trunk = new ArrayList<>();
        BlockPos current = base;

        while (trunk.size() < MAX_TREE_HEIGHT) {
            BlockState state = level.getBlockState(current);
            if (!state.is(BlockTags.LOGS)) {
                break;
            }
            trunk.add(current);
            current = current.above();
        }

        if (trunk.isEmpty()) {
            return;
        }

        Direction fallDirection = Direction.Plane.HORIZONTAL.getRandomDirection(RANDOM);
        togglingTrees.add(new TogglingTree(trunk, fallDirection));
    }

    /**
     * Call once per server tick to advance any trees currently toppling.
     * Cheap when nothing is toppling — returns immediately.
     */
    public static void tick(ServerLevel level) {
        if (togglingTrees.isEmpty()) {
            return;
        }

        var iterator = togglingTrees.iterator();
        while (iterator.hasNext()) {
            TogglingTree tree = iterator.next();

            if (tree.nextLogIndex >= tree.trunk.size()) {
                iterator.remove();
                continue;
            }

            if (tree.ticksUntilNextLog > 0) {
                tree.ticksUntilNextLog--;
                continue;
            }

            BlockPos logPos = tree.trunk.get(tree.nextLogIndex);
            BlockState state = level.getBlockState(logPos);

            if (state.is(BlockTags.LOGS)) {
                level.setBlock(logPos, Blocks.AIR.defaultBlockState(), 3);
                FallingBlockEntity falling = FallingBlockEntity.fall(level, logPos, state);
                falling.setDeltaMovement(
                        tree.fallDirection.getStepX() * 0.15,
                        0.05,
                        tree.fallDirection.getStepZ() * 0.15
                );
            }

            tree.nextLogIndex++;
            tree.ticksUntilNextLog = 2; // ~0.1s between logs falling, bottom to top
        }
    }

    private static final class TogglingTree {
        final List<BlockPos> trunk;
        final Direction fallDirection;
        int nextLogIndex = 0;
        int ticksUntilNextLog = 0;

        TogglingTree(List<BlockPos> trunk, Direction fallDirection) {
            this.trunk = trunk;
            this.fallDirection = fallDirection;
        }
    }
}
