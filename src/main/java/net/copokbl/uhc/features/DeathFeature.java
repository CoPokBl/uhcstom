package net.copokbl.uhc.features;

import net.copokbl.uhc.UhcUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.mangolise.combat.events.PlayerKilledEvent;
import net.mangolise.gamesdk.Game;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.copokbl.uhc.Uhc;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.NotNull;

public class DeathFeature implements Game.Feature<Uhc> {
    private Uhc uhc;

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerKilledEvent.class, this::playerDeath);
    }

    private void playerDeath(@NotNull PlayerKilledEvent event) {
        if (uhc.config().deathLightning()) {
            GameSdkUtils.strikeLightning(event.victim().getInstance(), event.victim().getPosition());
        }

        Player player = event.victim();
        Player killer = event.killer();

        Component msg;
        if (killer != null) {
            msg = ChatUtil.getDisplayName(player).append(ChatUtil.toComponent(" &7was killed by ").append(ChatUtil.getDisplayName(killer)));
        } else {
            msg = ChatUtil.getDisplayName(player).append(ChatUtil.toComponent(" &7died of their own accord"));
        }

        uhc.world().sendMessage(msg);
        uhc.players().remove(player);

        for (ItemStack item : player.getInventory().getItemStacks()) {
            if (item.isAir()) continue;
            UhcUtils.drop(player.getInstance(), player.getPosition(), item);
        }

        ItemStack head = ItemStack.of(Material.PLAYER_HEAD)
                .withCustomName(Component.text(player.getUsername() + "'s head").decoration(TextDecoration.ITALIC, false));
        PlayerSkin skin = player.getSkin();
        if (skin != null) {
            head = head.with(ItemComponent.PROFILE, new HeadProfile(player.getSkin()));
        }

        UhcUtils.drop(player.getInstance(), player.getPosition(), head);
    }
}
