package com.dari.endoftheworld.block;

import com.dari.endoftheworld.bunker.BunkerManager;
import com.dari.endoftheworld.bunker.BunkerSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * In-world trigger for {@link BunkerManager}: right-clicking with an empty
 * hand attempts to restore the given {@link BunkerSystem}. One class, one
 * {@link BunkerSystem} parameter per instance — replaces the earlier
 * generator-only GeneratorLeverBlock so the mod doesn't end up with six
 * near-identical block classes (one per bunker system).
 */
public class BunkerSystemLeverBlock extends Block {

    private final BunkerSystem system;

    public BunkerSystemLeverBlock(Properties properties, BunkerSystem system) {
        super(properties);
        this.system = system;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            BunkerManager.tryRestore(serverLevel, serverPlayer, system);
        }

        return InteractionResult.CONSUME;
    }
}
