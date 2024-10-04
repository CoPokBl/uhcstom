package net.copokbl.uhc.features;

import net.kyori.adventure.sound.Sound;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerEatEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EatingFeature implements Game.Feature<Game> {
    private static final Tag<Boolean> EATING_TAG = Tag.Boolean("is_eating").defaultValue(false);
    private static final List<Integer> FOODS = List.of(
            Material.GOLDEN_APPLE.id()
    );

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerEatEvent.class, this::playerEat);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, this::playerPacket);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerTickEvent.class, this::tickEatSound);
    }

    private void tickEatSound(@NotNull PlayerTickEvent event) {
        if (event.getInstance().getWorldAge() % 4 != 0) return;
        if (!event.getPlayer().getTag(EATING_TAG)) return;

        event.getInstance().playSoundExcept(event.getPlayer(), Sound.sound(SoundEvent.ENTITY_GENERIC_EAT, Sound.Source.PLAYER, 0.4f, 1f), event.getPlayer().getPosition());
    }

    private void playerPacket(@NotNull PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerDiggingPacket digPacket) {
            if (!digPacket.status().equals(ClientPlayerDiggingPacket.Status.UPDATE_ITEM_STATE)) return;

            // Stop eating
            event.getPlayer().setTag(EATING_TAG, false);
        }
        if (event.getPacket() instanceof ClientUseItemPacket useItem) {
            Material item = event.getPlayer().getItemInHand(useItem.hand()).material();
            if (!FOODS.contains(item.id())) return;

            // Start eating
            event.getPlayer().setTag(EATING_TAG, true);
        }
    }

    private void playerEat(@NotNull PlayerEatEvent event) {
        event.getPlayer().setItemInHand(event.getHand(), event.getItemStack().consume(1));
        event.getInstance().playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_BURP, Sound.Source.PLAYER, 0.3f, 1f), event.getPlayer().getPosition());
        event.getPlayer().setTag(EATING_TAG, false);

        Material material = event.getItemStack().material();
        if (material.equals(Material.GOLDEN_APPLE)) {
            event.getPlayer().addEffect(new Potion(PotionEffect.REGENERATION, (byte) 1, 20 * 6));
            event.getPlayer().addEffect(new Potion(PotionEffect.ABSORPTION, (byte) 0, 20 * 60 * 2));
        } else if (material.equals(Material.PLAYER_HEAD)) {
            event.getPlayer().addEffect(new Potion(PotionEffect.REGENERATION, (byte) 3, 20 * 5));
            event.getPlayer().addEffect(new Potion(PotionEffect.SPEED, (byte) 1, 20 * 8));
        }
    }
}
