package net.copokbl.uhc.state;

import net.copokbl.uhc.GameState;
import net.copokbl.uhc.state.states.GameEndedState;
import net.copokbl.uhc.state.states.InGameState;
import net.copokbl.uhc.state.states.PreGameState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum UhcState {
    PREGAME(new PreGameState()),
    IN_GAME(new InGameState()),
    ENDED(new GameEndedState());

    final GameState obj;

    UhcState(GameState obj) {
        this.obj = obj;
    }

    public GameState obj() {
        return obj;
    }
}
