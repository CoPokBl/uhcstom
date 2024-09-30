package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.potion.TimedPotion;
import org.jetbrains.annotations.NotNull;

public class RegenerationFeature implements Game.Feature<Game> {

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerTickEvent.class, this::tickRegen);
    }

    private void tickRegen(@NotNull PlayerTickEvent event) {
        if (event.getInstance().getWorldAge() % 20 != 0) return;  // Run once a second

        Player player = event.getPlayer();
        if (!player.hasEffect(PotionEffect.REGENERATION)) {
            return;
        }

        TimedPotion potion = player.getEffect(PotionEffect.REGENERATION);
        assert potion != null;
        player.setHealth((float) Math.min(player.getHealth() + potion.potion().amplifier() + 1, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
    }
}
