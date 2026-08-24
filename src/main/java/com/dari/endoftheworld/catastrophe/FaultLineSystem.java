package com.dari.endoftheworld.catastrophe;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Generates procedural fault-line canyons and karst sinkholes radiating from
 * an earthquake's epicenter — a mechanic with no vanilla Minecraft equivalent
 * (vanilla terrain never cracks open during play). A fault carves a
 * V-shaped canyon gradually outward along a random, slightly curving path
 * (a fixed number of steps per tick, not instant), and may punch a vertical
 * sinkhole shaft down to the first cave or void it hits partway along its
 * length.
 * <p>
 * Performance: each active fault advances a fixed, small number of steps per
 * tick and only touches blocks within its current cross-section — no
 * full-area or chunk scans, cost doesn't grow with the fault's length.
 */
public final class FaultLineSystem {

    private static final Random RANDOM = new Random();

    private static final int STEPS_PER_TICK = 1;
    private static final int MAX_STEPS = 40;
    private static final double STEP_LENGTH = 1.5;
    private static final double SURFACE_HALF_WIDTH = 2.5;
    private static final int MAX_DEPTH = 12;
    private static final double SINKHOLE_CHANCE_PER_STEP = 0.03;
    private static final int SINKHOLE_RADIUS = 2;
    private static final int SINKHOLE_MAX_DEPTH = 40;

    private final List<Fault> activeFaults = new ArrayList<>();

    public void start(BlockPos epicenter) {
        double angle = RANDOM.nextDouble() * Math.PI * 2.0;
        double curve = (RANDOM.nextDouble() - 0.5) * 0.15; // slight random bend per step
        activeFaults.add(new Fault(epicenter, angle, curve));
    }

    public void tick(ServerLevel level) {
        if (activeFaults.isEmpty()) {
            return;
        }

        Iterator<Fault> iterator = activeFaults.iterator();
        while (iterator.hasNext()) {
            Fault fault = iterator.next();

            if (fault.stepsDone >= MAX_STEPS) {
                iterator.remove();
                continue;
            }

            for (int i = 0; i < STEPS_PER_TICK && fault.stepsDone < MAX_STEPS; i++) {
                advanceFault(level, fault);
            }
        }
    }

    private void advanceFault(ServerLevel level, Fault fault) {
        fault.angle += fault.curve;
        fault.x += Math.cos(fault.angle) * STEP_LENGTH;
        fault.z += Math.sin(fault.angle) * STEP_LENGTH;
        fault.stepsDone++;

        int centerX = (int) Math.round(fault.x);
        int centerZ = (int) Math.round(fault.z);
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, centerX, centerZ);

        carveCanyonCrossSection(level, centerX, surfaceY, centerZ);

        if (RANDOM.nextDouble() < SINKHOLE_CHANCE_PER_STEP) {
            carveSinkhole(level, centerX, surfaceY, centerZ);
        }
    }

    private void carveCanyonCrossSection(ServerLevel level, int centerX, int surfaceY, int centerZ) {
        int radiusBlocks = (int) Math.ceil(SURFACE_HALF_WIDTH);

        for (int dx = -radiusBlocks; dx <= radiusBlocks; dx++) {
            for (int dz = -radiusBlocks; dz <= radiusBlocks; dz++) {
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                if (horizontalDist > SURFACE_HALF_WIDTH) {
                    continue;
                }

                // V-shaped profile: full width at the surface, narrowing with depth
                double widthFactor = 1.0 - (horizontalDist / SURFACE_HALF_WIDTH);
                int depthHere = (int) (MAX_DEPTH * widthFactor);

                for (int dy = 0; dy < depthHere; dy++) {
                    BlockPos pos = new BlockPos(centerX + dx, surfaceY - dy, centerZ + dz);
                    clearIfSafe(level, pos);
                }
            }
        }
    }

    private void carveSinkhole(ServerLevel level, int centerX, int surfaceY, int centerZ) {
        for (int dx = -SINKHOLE_RADIUS; dx <= SINKHOLE_RADIUS; dx++) {
            for (int dz = -SINKHOLE_RADIUS; dz <= SINKHOLE_RADIUS; dz++) {
                if (dx * dx + dz * dz > SINKHOLE_RADIUS * SINKHOLE_RADIUS) {
                    continue;
                }

                int x = centerX + dx;
                int z = centerZ + dz;

                for (int dy = 0; dy < SINKHOLE_MAX_DEPTH; dy++) {
                    BlockPos pos = new BlockPos(x, surfaceY - dy, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()) {
                        break; // hit a cave or void - the sinkhole connects here, stop digging
                    }

                    clearIfSafe(level, pos);
                }
            }
        }
    }

    private void clearIfSafe(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.isAir()
                || !state.getFluidState().isEmpty()
                || state.hasBlockEntity()
                || state.getDestroySpeed(level, pos) < 0) {
            return;
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    private static final class Fault {
        double x;
        double z;
        double angle;
        final double curve;
        int stepsDone = 0;

        Fault(BlockPos epicenter, double angle, double curve) {
            this.x = epicenter.getX();
            this.z = epicenter.getZ();
            this.angle = angle;
            this.curve = curve;
        }
    }
}
