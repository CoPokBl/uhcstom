package net.mangolise.uhc;

import net.mangolise.uhc.states.*;

public enum UhcState {
    PREGAME(new PreGame()),
    GRACE(new Grace()),
    PVP(new PvP()),
    MEETUP(null),
    ENDED(null);

    final GameState obj;

    UhcState(GameState obj) {
        this.obj = obj;
    }

    public GameState obj() {
        return obj;
    }
}
