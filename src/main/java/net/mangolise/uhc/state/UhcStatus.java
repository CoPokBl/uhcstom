package net.mangolise.uhc.state;

public class UhcStatus {
    private boolean pvp = false;
    private boolean meetup = false;
    private boolean lateJoinable = true;

    public boolean meetup() {
        return meetup;
    }

    public boolean pvp() {
        return pvp;
    }

    public boolean lateJoinable() {
        return lateJoinable;
    }

    public void setMeetup(boolean meetup) {
        this.meetup = meetup;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void setLateJoinable(boolean lateJoinable) {
        this.lateJoinable = lateJoinable;
    }
}
