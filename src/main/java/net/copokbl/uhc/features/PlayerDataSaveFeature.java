package net.copokbl.uhc.features;

import net.copokbl.uhc.PlayerData;
import net.copokbl.uhc.Uhc;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataSaveFeature implements Game.Feature<Uhc> {
    private final Map<String, PlayerData> data = new HashMap<>();

    @Override
    public void setup(Context<Uhc> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, e -> {
            Player p = e.getPlayer();
            PlayerData save = new PlayerData(p.getPosition(), p.getInventory().getItemStacks(), p.getHealth(), p.getFood(), p.getFoodSaturation());
            data.put(p.getUsername(), save);
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e ->
                MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
            Player p = e.getPlayer();
            if (!data.containsKey(p.getUsername())) {
                return;
            }

            PlayerData save = data.get(p.getUsername());
            p.teleport(save.pos());
            for (int i = 0; i < save.inventory().length; i++) {
                p.getInventory().setItemStack(i, save.inventory()[i]);
            }
            p.setHealth(save.health());
            p.setFood(save.food());
            p.setFoodSaturation(save.saturation());
        }));
    }
}
