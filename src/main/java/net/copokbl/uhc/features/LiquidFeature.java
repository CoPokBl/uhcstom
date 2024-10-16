package net.copokbl.uhc.features;

import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class LiquidFeature implements Game.Feature<BaseGame<?>> {
    private static final Tag<Integer> LEVEL_TAG = Tag.Integer("liquid_level").defaultValue(8);

    private static final Set<Point> surrounding = Set.of(
            new BlockVec(1, 0, 0), new BlockVec(-1, 0, 0),
            new BlockVec(0, 0, 1), new BlockVec(0, 0, -1)
    );

    @Override
    public void setup(Context<BaseGame<?>> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, e -> blockUpdate(e.getInstance(), e.getBlockPosition()));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, e -> blockUpdate(e.getInstance(), e.getBlockPosition()));
    }

    private static List<Point> getSurroundingBlocks(Point pos) {
        return List.of(
                pos.add(1, 0, 0),
                pos.add(-1, 0, 0),
                pos.add(0, 1, 0),
                pos.add(0, -1, 0),
                pos.add(0, 0, 1),
                pos.add(0, 0, -1)
        );
    }

    public static void blockUpdate(Instance instance, Point pos) {
        for (Point neighbour : getSurroundingBlocks(pos)) {
            Block block = instance.getBlock(neighbour);

            if (!block.compare(Block.WATER) && !block.compare(Block.LAVA)) {
                continue;
            }

            if (block.handler() == null || block.handler() instanceof BlockHandler.Dummy) {
                instance.setBlock(neighbour, block.withHandler(getSourceHandler(block)));
            }
        }
    }

    public static int getDefaultFlowSpeed(Block block) {
        return block.compare(Block.LAVA) ? 30 : 5;
    }

    public static boolean getDefaultInfiniteSource(Block block) {
        return !block.compare(Block.LAVA);
    }

    public static BlockHandler getSourceHandler(Block block, int flowSpeed, boolean canInfinite) {
        return new LiquidSourceHandler(block, flowSpeed, canInfinite);
    }

    public static BlockHandler getFlowingHandler(Block block, int flowSpeed, boolean canInfinite) {
        return new LiquidFlowHandler(block, flowSpeed, canInfinite);
    }

    public static BlockHandler getSourceHandler(Block block) {
        return new LiquidSourceHandler(block, getDefaultFlowSpeed(block), getDefaultInfiniteSource(block));
    }

    public static BlockHandler getFlowingHandler(Block block) {
        return new LiquidFlowHandler(block, getDefaultFlowSpeed(block), getDefaultInfiniteSource(block));
    }

    public static Block getWater() {
        return Block.WATER.withHandler(getSourceHandler(Block.WATER));
    }

    public static Block getLava() {
        return Block.LAVA.withHandler(getSourceHandler(Block.LAVA));
    }

    private static class LiquidHandler implements BlockHandler {
        protected static final Tag<Boolean> FALLING_TAG = Tag.Boolean("gamesdk_liquid_falling").defaultValue(false);

        protected final Block block;
        protected final int flowSpeed;
        protected final boolean canInfinite;

        public LiquidHandler(Block block, int flowSpeed, boolean canInfinite) {
            this.block = block;
            this.flowSpeed = flowSpeed;
            this.canInfinite = canInfinite;
        }

        @Override
        public @NotNull NamespaceID getNamespaceId() {
            return block.namespace();
        }

        @Override
        public boolean isTickable() {
            return true;
        }
    }

    private static class LiquidSourceHandler extends LiquidHandler {
        private final int movementCost;

        public LiquidSourceHandler(Block block, int flowSpeed, boolean canInfinite) {
            super(block, flowSpeed, canInfinite);
            movementCost = block.compare(Block.LAVA) ? 2 : 1;
        }

        @Override
        public void tick(@NotNull Tick tick) {
            if (tick.getInstance().getWorldAge() % flowSpeed != 0) return;

            Instance instance = tick.getInstance();
            Point blockPos = tick.getBlockPosition();
            Block block = tick.getBlock();

            for (Point pos : surrounding) {
                pos = blockPos.add(pos);
                tryPlaceBlock(instance, pos, 8 - movementCost, () -> block.withTag(FALLING_TAG, false)
                        .withHandler(new LiquidFlowHandler(block, flowSpeed, canInfinite)));
            }

            tryPlaceBlock(instance, blockPos.sub(0, 1, 0), 8 - movementCost, () -> block.withTag(FALLING_TAG, true)
                    .withHandler(new LiquidFlowHandler(block, flowSpeed, canInfinite)));
        }
    }

    private static class LiquidFlowHandler extends LiquidHandler {
        private final int movementCost;

        public LiquidFlowHandler(Block block, int flowSpeed, boolean canInfinite) {
            super(block, flowSpeed, canInfinite);
            movementCost = block.compare(Block.LAVA) ? 2 : 1;
        }

        @Override
        public void tick(@NotNull Tick tick) {
            if (tick.getInstance().getWorldAge() % flowSpeed != 0) return;

            int level = getLevel(tick.getBlock());

            Instance instance = tick.getInstance();
            Point blockPos = tick.getBlockPosition();
            Block block = tick.getBlock();
            boolean falling = block.getTag(FALLING_TAG);

            // check if there is a water block for this water to come from
            if (falling ? !hasUpSupportingBlock(instance, blockPos) : horizSupportingBlockCount(instance, blockPos, level, movementCost) == 0) {
                if (falling || level == movementCost) {
                    instance.setBlock(blockPos, Block.AIR);
                } else {
                    placeBlock(instance, blockPos, level - movementCost, block.withHandler(this));
                }
                return;
            }

            // if there are 2 nearby source blocks, this become s a source block
            if (!falling && canInfinite && horizSupportingBlockCount(instance, blockPos, 7, movementCost) >= 2) {
                placeBlock(instance, blockPos, 8, block.withHandler(new LiquidSourceHandler(block, flowSpeed, canInfinite)));
            }

            // try placing below
            Point belowPos = tick.getBlockPosition().sub(0, 1, 0);
            Block belowBlock = getBlockDefault(instance, belowPos);

            if (belowBlock.compare(block)) {
                return;
            }

            if (tryPlaceBlock(instance, belowPos, belowBlock, 8 - movementCost, () -> block.withTag(FALLING_TAG, true)
                    .withHandler(new LiquidFlowHandler(block, flowSpeed, canInfinite)))) {
                return;
            }

            // if not, then go horizontally
            if (level > movementCost) {
                level -= movementCost;
                for (Point pos : surrounding) {
                    pos = blockPos.add(pos);
                    tryPlaceBlock(instance, pos, level, () -> block.withTag(FALLING_TAG, false)
                            .withHandler(new LiquidFlowHandler(block, flowSpeed, canInfinite)));
                }
            }
        }

        private int horizSupportingBlockCount(Instance instance, Point blockPos, int level, int moveCost) {
            int count = 0;

            for (Point pos : surrounding) {
                pos = blockPos.add(pos);
                Block neighbor = getBlockDefault(instance, pos);
                if (!neighbor.compare(block)) {
                    continue;
                }

                int nLevel = getLevel(neighbor);
                if (nLevel == level + moveCost) {
                    count++;
                }
            }

            return count;
        }

        private boolean hasUpSupportingBlock(Instance instance, Point blockPos) {
            blockPos = blockPos.add(0, 1, 0);
            Block neighbor = getBlockDefault(instance, blockPos);
            return neighbor.compare(block);
        }
    }

    private static boolean tryPlaceBlock(Instance instance, Point pos, int level, Supplier<Block> block) {
        return tryPlaceBlock(instance, pos, getBlockDefault(instance, pos), level, block);
    }

    // Level has to be inverted for the client for some reason
    private static boolean tryPlaceBlock(Instance instance, Point pos, Block currentBlock, int level, Supplier<Block> block) {
        boolean isSame = currentBlock.compare(block.get());
        if ((!currentBlock.isSolid() && !currentBlock.isLiquid()) || (isSame && getLevel(currentBlock) < level)) {
            if (!currentBlock.isSolid()) {
                BlockLootFeature.dropLoot(instance, currentBlock, pos);
            }
            placeBlock(instance, pos, level, block.get());
            return true;
        }

        return false;
    }

    private static void placeBlock(Instance instance, Point pos, int level, Block block) {
        if (pos.y() >= instance.getCachedDimensionType().maxY() || pos.y() < instance.getCachedDimensionType().minY()) {
            return;
        }

        if (level > 8 || level < 0) {
            throw new AssertionError("Requires: 0 <= level <= 8");
        }
        instance.setBlock(pos, block.withTag(LEVEL_TAG, level).withProperty("level", String.valueOf(8-level)));
        blockUpdate(instance, pos);
    }

    // Level has to be inverted for the client for some reason
    public static int getLevel(Block block) {
        return block.getTag(LEVEL_TAG);
    }

    private static Block getBlockDefault(Instance instance, Point pos) {
        if (ChunkUtils.isLoaded(instance, pos)) {
            return instance.getBlock(pos);
        }

        return Block.STONE;
    }
}
