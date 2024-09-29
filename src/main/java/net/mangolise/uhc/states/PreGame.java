package net.mangolise.uhc.states;

import net.mangolise.uhc.GameState;
import net.mangolise.uhc.Uhc;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerMoveEvent;

public class PreGame extends GameState {

    @Override
    public void start(Uhc uhc) {
        listen(PlayerMoveEvent.class, this::onMove);
        listen(PlayerBlockBreakEvent.class, e -> e.setCancelled(true));
    }

    private void onMove(PlayerMoveEvent e) {
        e.setNewPosition(e.getPlayer().getPosition().withView(e.getNewPosition()).withY(e.getNewPosition().y()));
    }
}
