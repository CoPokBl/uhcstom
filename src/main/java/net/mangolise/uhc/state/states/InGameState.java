package net.mangolise.uhc.state.states;

import net.mangolise.uhc.GameState;
import net.mangolise.uhc.Uhc;
import net.mangolise.uhc.state.UhcEvent;
import net.minestom.server.event.instance.InstanceTickEvent;

public class InGameState extends GameState {
    private Uhc uhc;

    @Override
    public void start(Uhc uhc) {
        this.uhc = uhc;
        uhc.setGameStartTime(System.currentTimeMillis());

        listen(InstanceTickEvent.class, this::onTick);
    }

    private void onTick(InstanceTickEvent event) {
        if (event.getInstance() != uhc.world()) {
            return;
        }

        if (uhc.world().getWorldAge() % 20 != 0) {  // Run once a second
            return;
        }

        UhcEvent nextEvent = uhc.getNextEvent();
        if (nextEvent == null) {
            // No more events
            return;
        }

        long startTime = uhc.gameStartTime() + nextEvent.startTime().toMillis();
        if (startTime > System.currentTimeMillis()) {
            return;
        }

        // Start it
        uhc.triggerEvent(nextEvent);
    }
}
