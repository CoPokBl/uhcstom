package net.mangolise.uhc.state;

import net.mangolise.uhc.Uhc;

import java.time.Duration;

public interface UhcEvent {
    Duration startTime();
    String displayName();
    void start(Uhc uhc);
}
