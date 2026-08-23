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
 * First real in-world trigger for {@link BunkerManager}: right-clicking this
 * block with an empty hand attempts to restore {@link BunkerSystem#GENERATOR}.
 * <p>
 * Confirmed method: useWithoutItem(BlockState, Level, BlockPos, Player, BlockHitResult)
 * -> InteractionResult is the current empty-hand-right-click hook, cross-checked
 * against several official 1.21.x block classes (FlowerPotBlock, StructureBlock,
 * CommandBlock all override it identically) — higher confidence than usual here.
 * Registration in ModBlocks.java is the less certain part of this stage.
 */
public class GeneratorLeverBlock extends Block {

    public GeneratorLeverBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            BunkerManager.tryRestore(serverLevel, serverPlayer, BunkerSystem.GENERATOR);
        }

        return InteractionResult.CONSUME;
    }
}
