package net.copokbl.uhc;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class UhcUtils {

    public static Entity drop(Instance world, Point pos, ItemStack stack) {
        Entity item = new Entity(EntityType.ITEM);
        item.editEntityMeta(ItemEntityMeta.class, e -> e.setItem(stack));
        item.setInstance(world, new Pos(pos));
        double x = ThreadLocalRandom.current().nextDouble();
        double z = ThreadLocalRandom.current().nextDouble();
        item.setVelocity(new Vec(x, 2, z).mul(2));
        return item;
    }

    public static Set<Integer> getBlockIdsPlayerIsStandingOnAndAbove(Player p, int upAmount, boolean ignoreFeet) {
        Set<Integer> idSet = new HashSet<>();
        for (int i = ignoreFeet ? 1 : 0; i < upAmount; i++) {
            idSet.addAll(getBlockIdsAtPlayerFeet(p, i-0.1));
        }

        return idSet;
    }

    public static Set<Integer> getBlockIdsAtPlayerFeet(Player p, double yOffset) {
        Pos playerLocation = p.getPosition().add(0, yOffset, 0);
        Set<BlockVec> points = new HashSet<>();
        BoundingBox box = p.getBoundingBox();

        // get the rounded points for all four corners and add them to a set to remove duplicates
        // center isn't needed because the player is less a block wide
        points.add(new BlockVec(playerLocation.add(box.minX(), box.minY(), box.minZ())));
        points.add(new BlockVec(playerLocation.add(box.minX(), box.minY(), box.maxZ())));
        points.add(new BlockVec(playerLocation.add(box.maxX(), box.minY(), box.minZ())));
        points.add(new BlockVec(playerLocation.add(box.maxX(), box.minY(), box.maxZ())));

        Set<Integer> blockIds = new HashSet<>(points.size());
        for (Point point : points) {
            blockIds.add(p.getInstance().getBlock(point).id());
        }

        return blockIds;
    }
}
