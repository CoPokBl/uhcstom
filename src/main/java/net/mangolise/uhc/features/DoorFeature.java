package net.mangolise.uhc.features;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class DoorFeature implements Game.Feature<Game> {
    private static final Set<Integer> doorIds = Set.of(
            Block.CHERRY_DOOR.id(),
            Block.OXIDIZED_COPPER_DOOR.id(),
            Block.BIRCH_DOOR.id(),
            Block.WAXED_WEATHERED_COPPER_DOOR.id(),
            Block.IRON_DOOR.id(),
            Block.DARK_OAK_DOOR.id(),
            Block.BAMBOO_DOOR.id(),
            Block.WEATHERED_COPPER_DOOR.id(),
            Block.WAXED_COPPER_DOOR.id(),
            Block.ACACIA_DOOR.id(),
            Block.COPPER_DOOR.id(),
            Block.SPRUCE_DOOR.id(),
            Block.WARPED_DOOR.id(),
            Block.EXPOSED_COPPER_DOOR.id(),
            Block.WAXED_OXIDIZED_COPPER_DOOR.id(),
            Block.WAXED_EXPOSED_COPPER_DOOR.id(),
            Block.CRIMSON_DOOR.id(),
            Block.JUNGLE_DOOR.id(),
            Block.OAK_DOOR.id(),
            Block.MANGROVE_DOOR.id()
    );

    private static final Set<Integer> trapdoorIds = Set.of(
            Block.WEATHERED_COPPER_TRAPDOOR.id(),
            Block.WAXED_OXIDIZED_COPPER_TRAPDOOR.id(),
            Block.WAXED_WEATHERED_COPPER_TRAPDOOR.id(),
            Block.BAMBOO_TRAPDOOR.id(),
            Block.DARK_OAK_TRAPDOOR.id(),
            Block.SPRUCE_TRAPDOOR.id(),
            Block.WAXED_EXPOSED_COPPER_TRAPDOOR.id(),
            Block.JUNGLE_TRAPDOOR.id(),
            Block.IRON_TRAPDOOR.id(),
            Block.WARPED_TRAPDOOR.id(),
            Block.ACACIA_TRAPDOOR.id(),
            Block.OAK_TRAPDOOR.id(),
            Block.OXIDIZED_COPPER_TRAPDOOR.id(),
            Block.MANGROVE_TRAPDOOR.id(),
            Block.COPPER_TRAPDOOR.id(),
            Block.CRIMSON_TRAPDOOR.id(),
            Block.BIRCH_TRAPDOOR.id(),
            Block.WAXED_COPPER_TRAPDOOR.id(),
            Block.EXPOSED_COPPER_TRAPDOOR.id()
    );

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, this::blockInteract);
    }

    private void blockInteract(PlayerBlockInteractEvent event) {
        Block block = event.getBlock();
        if (doorIds.contains(block.id())) {
            BlockVec topHalf = event.getBlockPosition();
            BlockVec bottomHalf;

            if (block.getProperty("half").equals("upper")) {
                bottomHalf = topHalf.sub(0, 1, 0);
            } else {
                bottomHalf = topHalf;
                topHalf = topHalf.add(0, 1, 0);
            }

            Block newBlock = invertOpen(block);

            event.getInstance().setBlock(bottomHalf, newBlock.withProperty("half", "lower"));
            event.getInstance().setBlock(topHalf, newBlock.withProperty("half", "upper"));
        }
        else if (trapdoorIds.contains(block.id())) {
            BlockVec pos = event.getBlockPosition();
            Block newBlock = invertOpen(block);

            event.getInstance().setBlock(pos, newBlock);
        }
    }

    private Block invertOpen(Block block) {
        if (block.getProperty("open").equals("true")) {
            return block.withProperty("open", "false");
        } else {
            return block.withProperty("open", "true");
        }
    }
}
