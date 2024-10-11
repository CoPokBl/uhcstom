package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CustomPlaceablesFeatures implements Game.Feature<Game> {
    private static final Map<Material, PlacementRule> PLACEABLES = new HashMap<>() {{
        put(Material.FLINT_AND_STEEL, new PlacementRule(() -> Block.FIRE));
        put(Material.WATER_BUCKET, new PlacementRule(LiquidFeature::getWater, Material.BUCKET));
        put(Material.LAVA_BUCKET, new PlacementRule(LiquidFeature::getLava, Material.BUCKET));
    }};

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, this::onInteract);
    }

    private void onInteract(@NotNull PlayerBlockInteractEvent event) {
        ItemStack item = event.getPlayer().getItemInHand(event.getHand());
        if (item.isAir()) {
            return;
        }

        PlacementRule place = PLACEABLES.get(item.material());
        if (place == null) {
            return;
        }

        Direction dir = event.getBlockFace().toDirection();
        BlockVec pos = event.getBlockPosition().add(dir.normalX(), dir.normalY(), dir.normalZ());

        Block newBlock = place.block.get();
        if (newBlock == null) {
            throw new IllegalStateException();
        }
        MinecraftServer.getSchedulerManager().scheduleNextTick(() ->
                event.getInstance().setBlock(pos, newBlock));

        if (place.newItem != null) {
            event.getPlayer().setItemInHand(event.getHand(), event.getPlayer().getItemInHand(event.getHand()).withMaterial(place.newItem));
        }
    }

    private record PlacementRule(Supplier<Block> block, @Nullable Material newItem) {
        PlacementRule(Supplier<Block> block) {
            this(block, null);
        }
    }
}
