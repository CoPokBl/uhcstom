package net.mangolise.uhc.features;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplacableBlockFeature implements Game.Feature<Game> {
    @Override
    public void setup(Context<Game> context) {
        BlockManager blockManager = MinecraftServer.getBlockManager();

        Block.values().stream().filter(block -> block.registry().isReplaceable()).forEach(block -> {
            blockManager.registerBlockPlacementRule(new ReplacementPlacementRule(block));
        });
    }

    private static class ReplacementPlacementRule extends BlockPlacementRule {
        public ReplacementPlacementRule(Block block) {
            super(block);
        }

        @Override
        public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState placementState) {
            return null;
        }

        @Override
        public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
            return true;
        }
    }
}
