package net.copokbl.uhc.state.events;

import net.copokbl.uhc.Uhc;
import net.copokbl.uhc.state.UhcEvent;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

/**
 * An event that enabled PvP at a specific time and prevents new people joining.
 */
public record PvPEvent(Duration startTime) implements UhcEvent {

    @Override
    public String displayName() {
        return "PvP";
    }

    @Override
    public void start(Uhc uhc) {
        uhc.status().setPvp(true);
        uhc.status().setLateJoinable(false);
        uhc.broadcastTitle("&cPvP is now enabled", "Be careful!");
        uhc.broadcastSound(SoundEvent.ENTITY_ENDER_DRAGON_GROWL);
    }
}
