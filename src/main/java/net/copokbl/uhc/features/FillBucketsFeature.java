package net.copokbl.uhc.features;

import net.copokbl.uhc.Uhc;
import net.copokbl.uhc.UhcUtils;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.block.BlockIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class FillBucketsFeature implements Game.Feature<Uhc> {
    private Uhc uhc;

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemEvent.class, this::blockInteract);
    }

    private Point getTargetBlock(Player player) {
        Iterator<Point> it = new BlockIterator(player, 5);
        while (it.hasNext()) {
            final Point position = it.next();
            final Block block = player.getInstance().getBlock(position);
            if ((block.compare(Block.WATER) || block.compare(Block.LAVA)) && !"0".equals(block.getProperty("level"))) continue;
            if (!block.isAir()) return position;
        }

        return null;
    }

    private void blockInteract(@NotNull PlayerUseItemEvent event) {
        if (event.getItemStack().material() != Material.BUCKET) return;
        if (event.isCancelled()) return;

        MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {

        });

        Player player = event.getPlayer();
        Point targetPos = getTargetBlock(player);
        if (targetPos == null) return;

        Block block = event.getInstance().getBlock(targetPos);
        if (block.compare(Block.WATER)) {
            if (event.getItemStack().amount() == 1) {
                player.setItemInHand(event.getHand(), ItemStack.of(Material.WATER_BUCKET));
            } else {
                player.setItemInHand(event.getHand(), event.getItemStack().consume(1));
                UhcUtils.addItemOrDrop(player, ItemStack.of(Material.WATER_BUCKET));
            }
        }

        else if (block.compare(Block.LAVA)) {
            if (event.getItemStack().amount() == 1) {
                player.setItemInHand(event.getHand(), ItemStack.of(Material.LAVA_BUCKET));
            } else {
                player.setItemInHand(event.getHand(), event.getItemStack().consume(1));
                UhcUtils.addItemOrDrop(player, ItemStack.of(Material.LAVA_BUCKET));
            }
        }

        else {
            return;
        }

        event.getInstance().setBlock(targetPos, Block.AIR);
        uhc.blockUpdate(event.getInstance(), UhcUtils.toBlockPos(targetPos));
    }
}
