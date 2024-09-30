package net.copokbl.uhc.features;

import net.mangolise.combat.events.PlayerAttackEvent;
import net.mangolise.gamesdk.Game;
import net.mangolise.gamesdk.util.ChatUtil;
import net.copokbl.uhc.Uhc;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class CombatFeature implements Game.Feature<Uhc> {
    private Uhc uhc;

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerAttackEvent.class, this::playerAttack);
    }

    private void playerAttack(@NotNull PlayerAttackEvent event) {
        if (!uhc.status().pvp()) {
            event.setCancelled(true);
            event.attacker().sendMessage(ChatUtil.toComponent("&cPvP is not enabled yet!"));
        }
    }
}
