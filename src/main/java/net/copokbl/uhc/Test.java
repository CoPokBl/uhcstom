package net.copokbl.uhc;

import net.mangolise.gamesdk.util.GameSdkUtils;
import net.copokbl.uhc.state.events.PvPEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.permission.Permission;

import java.time.Duration;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        System.setProperty("minestom.chunk-view-distance", "12");

        MinecraftServer server = MinecraftServer.init();

        if (GameSdkUtils.useBungeeCord()) {
            BungeeCordProxy.enable();
        }

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, e -> {
            e.getPlayer().setPermissionLevel(4);
            e.getPlayer().addPermission(new Permission("*"));
        });

        Uhc uhc = new Uhc(new Uhc.Config(500, 1, true, "uhcstom", List.of(
                new PvPEvent(Duration.ofSeconds(20))
        )));
        uhc.setup();

        server.start("0.0.0.0", GameSdkUtils.getConfiguredPort());
    }
}
