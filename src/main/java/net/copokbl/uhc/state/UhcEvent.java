package net.copokbl.uhc.state;

import net.copokbl.uhc.Uhc;

import java.time.Duration;

public interface UhcEvent {
    Duration startTime();
    String displayName();
    void start(Uhc uhc);
}
