package net.mangolise.uhc.states;

import net.mangolise.combat.events.PlayerAttackEvent;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.uhc.GameState;
import net.mangolise.uhc.Uhc;

public class Grace extends GameState {

    @Override
    public void start(Uhc uhc) {
        listen(PlayerAttackEvent.class, this::onAttack);
    }

    private void onAttack(PlayerAttackEvent e) {
        e.setCancelled(true);
        e.attacker().sendMessage(ChatUtil.toComponent("&cPvP is not enabled yet!"));
    }
}
