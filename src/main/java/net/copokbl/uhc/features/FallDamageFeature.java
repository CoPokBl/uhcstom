package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.copokbl.uhc.UhcUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FallDamageFeature implements Game.Feature<Game> {
    private static final Tag<Double> FALL_DISTANCE_TAG = Tag.Double("fall_distance").defaultValue(0D);
    private static final Set<Integer> NO_FALL_DAMAGE_BLOCKS = Set.of(
            Block.WATER.id(),
            Block.LAVA.id(),
            Block.SLIME_BLOCK.id(),
            Block.HONEY_BLOCK.id(),
            Block.COBWEB.id(),
            Block.VINE.id(),
            Block.LADDER.id(),
            Block.WEEPING_VINES.id(),
            Block.TWISTING_VINES.id(),
            Block.WEEPING_VINES_PLANT.id(),
            Block.TWISTING_VINES_PLANT.id()
    );

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, this::onMove);
    }

    private void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isAllowFlying() ||
                player.isFlyingWithElytra() ||
                UhcUtils.getBlockIdsPlayerIsStandingOnAndAbove(player, 3, false).stream().anyMatch(NO_FALL_DAMAGE_BLOCKS::contains)) {
            player.removeTag(FALL_DISTANCE_TAG);
            return;
        }

        double movement = Math.max(0, event.getPlayer().getPosition().y() - event.getNewPosition().y());
        double fallDistance = player.updateAndGetTag(FALL_DISTANCE_TAG, val -> val + movement);

        if (!player.isOnGround()) {
            return;
        }

        player.removeTag(FALL_DISTANCE_TAG);

        if (fallDistance <= 4) {
            return;
        }

        float damage = (float) (fallDistance - 3f) / 2;
        Damage dmgObj = new Damage(DamageType.FALL, null, null, null, damage);

        player.damage(dmgObj);
    }
}
