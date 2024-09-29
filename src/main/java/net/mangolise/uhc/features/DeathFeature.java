package net.mangolise.uhc.features;

import net.kyori.adventure.text.Component;
import net.mangolise.combat.events.PlayerKilledEvent;
import net.mangolise.gamesdk.Game;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.mangolise.uhc.Uhc;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeathFeature implements Game.Feature<Uhc> {
    private Uhc uhc;

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerKilledEvent.class, this::playerDeath);
    }

    private void playerDeath(@NotNull PlayerKilledEvent event) {
        GameSdkUtils.strikeLightning(event.victim().getInstance(), event.victim().getPosition());

        Player player = event.victim();
        Player killer = event.killer();

        Component msg;
        if (killer != null) {
            msg = ChatUtil.getDisplayName(player).append(ChatUtil.toComponent(" &7was killed by " + ChatUtil.getDisplayName(killer)));
        } else {
//            String a = switch (event.lastDamage().getType()) {
//                case DamageType.ARROW -> "";
//                default -> throw new IllegalStateException("Unexpected value: " + event.lastDamage().getType());
//            };
//            msg = ChatUtil.getDisplayName(player).append();
        }
    }
}
