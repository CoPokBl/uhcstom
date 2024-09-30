package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.gamesdk.util.SidebarBuilder;
import net.copokbl.uhc.Uhc;
import net.copokbl.uhc.state.UhcEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class ScoreboardFeature implements Game.Feature<Uhc> {
    private Sidebar sidebar;
    private Uhc uhc;

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        sidebar = new Sidebar(ChatUtil.toComponent("&b<<< UHC >>>"));
        MinecraftServer.getSchedulerManager().scheduleTask(this::updateScoreboard, TaskSchedule.seconds(1), TaskSchedule.seconds(1));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, this::playerJoin);
    }

    private void playerJoin(@NotNull PlayerSpawnEvent event) {
        sidebar.addViewer(event.getPlayer());
    }

    private void updateScoreboard() {
        SidebarBuilder builder = new SidebarBuilder()
                .addLine(ChatUtil.toComponent("&0----------------------"));

        switch (uhc.state()) {
            case PREGAME -> builder.addLine(ChatUtil.toComponent("&eWAITING"));
            case IN_GAME -> {
                UhcEvent nextEvent = uhc.getNextEvent();
                String eventDisplay = "None";
                if (nextEvent != null) {
                    long seconds = nextEvent.startTime().minus(Duration.ofMillis(System.currentTimeMillis() - uhc.gameStartTime())).toSeconds() + 1;
                    eventDisplay = nextEvent.displayName() + " in " + seconds + " seconds";
                }
                builder.addLine(ChatUtil.toComponent("&eNext event: " + eventDisplay))
                        .addLine(ChatUtil.toComponent("&eRemaining Players: " + uhc.players().size()));
            }
        }

        builder.addLine(ChatUtil.toComponent("&a&0----------------------"))
                .addLine(ChatUtil.toComponent("&e" + uhc.config().ip()));
        builder.apply(sidebar);
    }
}
