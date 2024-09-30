package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class FallingBlocksFeature implements Game.Feature<Game> {
    private final ItemDropper dropper;
    private static final Set<Point> surrounding = Set.of(
            new BlockVec(1, 0, 0), new BlockVec(-1, 0, 0),
            new BlockVec(0, 0, 1), new BlockVec(0, 0, -1)
    );

    private static final Set<Integer> FALL_BLOCKS = Set.of(
            Block.SAND.id(),
            Block.RED_SAND.id(),
            Block.GRAVEL.id(),
            Block.BLUE_CONCRETE_POWDER.id(),
            Block.RED_CONCRETE_POWDER.id(),
            Block.BLACK_CONCRETE_POWDER.id(),
            Block.YELLOW_CONCRETE_POWDER.id(),
            Block.ORANGE_CONCRETE_POWDER.id(),
            Block.PURPLE_CONCRETE_POWDER.id(),
            Block.BROWN_CONCRETE_POWDER.id(),
            Block.GREEN_CONCRETE_POWDER.id(),
            Block.LIGHT_GRAY_CONCRETE_POWDER.id(),
            Block.CYAN_CONCRETE_POWDER.id(),
            Block.GRAY_CONCRETE_POWDER.id(),
            Block.PINK_CONCRETE_POWDER.id(),
            Block.LIGHT_BLUE_CONCRETE_POWDER.id(),
            Block.MAGENTA_CONCRETE_POWDER.id(),
            Block.WHITE_CONCRETE_POWDER.id()
    );

    public interface ItemDropper {
        void run(Block block, Point pos);
    }

    public FallingBlocksFeature(ItemDropper itemDropper) {
        dropper = itemDropper;
    }

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, e ->
                MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> blockUpdate(e.getInstance(), e.getBlockPosition())));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, e ->
                MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> blockUpdate(e.getInstance(), e.getBlockPosition())));
        MinecraftServer.getGlobalEventHandler().addListener(EntityTickEvent.class, this::entityTick);
    }

    private boolean isSupporting(Block block) {
        return !block.isAir() && !block.isLiquid();
    }

    private void entityTick(@NotNull EntityTickEvent event) {
        Entity entity = event.getEntity();
        if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;

        Block below = event.getInstance().getBlock(entity.getPosition().sub(0, 0.5, 0));
        if (!isSupporting(below) || below.registry().isReplaceable()) return;

        FallingBlockMeta meta = (FallingBlockMeta) entity.getEntityMeta();

        if (!below.isSolid()) {
            // break
            dropper.run(meta.getBlock(), entity.getPosition());
            entity.remove();
            return;
        }

        event.getInstance().setBlock(entity.getPosition(), meta.getBlock());
        entity.remove();
    }

    private static List<Point> getUpdateTargets(Point pos) {
        return List.of(
                pos,
                pos.add(1, 0, 0),
                pos.add(-1, 0, 0),
                pos.add(0, 1, 0),
                pos.add(0, -1, 0),
                pos.add(0, 0, 1),
                pos.add(0, 0, -1)
        );
    }

    private void blockUpdate(Instance instance, Point pos) {
        for (Point neighbour : getUpdateTargets(pos)) {
            Block block = instance.getBlock(neighbour);

            if (!FALL_BLOCKS.contains(block.id())) {
                continue;
            }

            Block below = instance.getBlock(neighbour.sub(0, 1, 0));
            if (isSupporting(below)) {
                continue;
            }

            // Make it fall
            Entity fallingBlock = new Entity(EntityType.FALLING_BLOCK);
            fallingBlock.editEntityMeta(FallingBlockMeta.class, meta -> meta.setBlock(block));
            instance.setBlock(neighbour, Block.AIR);
            fallingBlock.setInstance(instance, neighbour.add(0.5, 0, 0.5));
            blockUpdate(instance, neighbour);
        }
    }
}
