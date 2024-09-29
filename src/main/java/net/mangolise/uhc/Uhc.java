package net.mangolise.uhc;

import net.mangolise.combat.CombatConfig;
import net.mangolise.combat.MangoCombat;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.features.*;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.mangolise.gamesdk.util.PerformanceTracker;
import net.mangolise.gamesdk.util.Timer;
import net.mangolise.uhc.features.*;
import net.mangolise.uhc.features.LiquidFeature;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Uhc extends BaseGame<Uhc.Config> {
    private final Config config;
    private UhcState state = UhcState.PREGAME;
    private final List<Player> players = new ArrayList<>();
    private Instance world;

    public Uhc(Config config) {
        super(config);
        this.config = config;
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(
                new BlockLoot(),
                new ItemPickupFeature(),
                new Crafting(),
                new DoorFeature(),
                new AdminCommandsFeature(),
                new ReplacableBlockFeature(),
                new FallDamage(),
                new CustomPlaceables(),
                new SignFeature(),
                new PlayerHeadFeature(),
                new ChestsFeature(),
                new JukeboxFeature(),
                new DropFeature(),
                new LiquidFeature()
        );
    }

    @Override
    public void setup() {
        super.setup();

        world = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader("uhc"));
        world.enableAutoChunkLoad(true);
        world.setWorldBorder(new WorldBorder(config.worldRadius * 2, 0, 0, 10, 15));

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, e -> e.setSpawningInstance(world));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e -> playerJoin(e.getPlayer()));

        state.obj().start(this);

        MangoCombat.enableGlobal(CombatConfig.create());
        PerformanceTracker.start();
    }

    public Instance world() {
        return world;
    }

    private void broadcast(String message) {
        world.sendMessage(ChatUtil.toComponent(message));
    }

    private void changeState(UhcState state) {
        this.state.obj().stop();
        this.state = state;
        this.state.obj().start(this);
    }

    public void startGame() {
        Timer.countDown(1, i -> broadcast("&aThe game will start in &6" + i + "&a seconds!")).thenAccept(v -> {
            internalStartGame();
        });
    }

    private void internalStartGame() {
        changeState(UhcState.GRACE);
        broadcast("&aThe game has started!");
    }

    /**
     * Add the player as a participant in the game.
     * @param player The player to add.
     */
    private void playerInit(Player player) {
        players.add(player);

        // Get a position
        Random random = ThreadLocalRandom.current();
        int x = random.nextInt(-config.worldRadius, config.worldRadius);
        int z = random.nextInt(-config.worldRadius, config.worldRadius);

        Point load = new Vec(x, 0, z);
        if (!world.isChunkLoaded(load)) {
            world.loadChunk(load).join();
        }

        int y = GameSdkUtils.getHighestBlock(world, x, z);

        player.teleport(new Pos(x + 0.5, y + 4, z + 0.5));
        world.setBlock(x, y, z, Block.STONE, true);
        world.sendMessage(ChatUtil.getDisplayName(player).append(ChatUtil.toComponent(" &7has joined the game!")));
    }

    public void playerJoin(Player player) {
        switch (state) {
            case PREGAME -> {
                playerInit(player);

                if (players.size() == config.minPlayers) {
                    startGame();
                }
            }

            case GRACE -> playerInit(player);

            default -> {  // Spec
                player.sendMessage(ChatUtil.toComponent("&cThe game has already started so you have been made a spectator"));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public record Config(int worldRadius, int minPlayers) { }
}
