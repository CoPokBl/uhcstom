package net.copokbl.uhc;

import net.copokbl.uhc.features.*;
import net.copokbl.uhc.features.LiquidFeature;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.mangolise.combat.CombatConfig;
import net.mangolise.combat.MangoCombat;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.features.*;
import net.mangolise.gamesdk.util.ChatUtil;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.mangolise.gamesdk.util.PerformanceTracker;
import net.mangolise.gamesdk.util.Timer;
import net.copokbl.uhc.state.UhcEvent;
import net.copokbl.uhc.state.UhcState;
import net.copokbl.uhc.state.UhcStatus;
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
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class Uhc extends BaseGame<Uhc.Config> {
    private final CompletableFuture<Void> preShutdown = new CompletableFuture<>();
    private final Config config;
    private UhcState state = UhcState.PREGAME;
    private final UhcStatus status = new UhcStatus();
    private final List<Player> players = new ArrayList<>();
    private Instance world;
    private List<UhcEvent> events;
    private long gameStartTime = -1;

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
                new ItemDropFeature(),
                new LiquidFeature(),
                new CombatFeature(),
                new ScoreboardFeature(),
                new FallingBlocksFeature(BlockLoot::dropLoot),
                new EatingFeature(),
                new RegenerationFeature(),
                new FillBucketsFeature()
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

        events = new ArrayList<>(config.events);

        MangoCombat.enableGlobal(CombatConfig.create());
        PerformanceTracker.start();

        state.obj().start(this);
    }

    public void blockUpdate(Instance instance, Point pos) {
        LiquidFeature.blockUpdate(instance, pos);
        feature(FallingBlocksFeature.class).blockUpdate(instance, pos);
    }

    public Instance world() {
        return world;
    }

    public void broadcast(String message) {
        world.sendMessage(ChatUtil.toComponent(message));
    }

    public void broadcastTitle(@Nullable String title, @Nullable String sub) {
        Component titleComp = title == null ? Component.text("") : ChatUtil.toComponent(title);
        Component subComp = sub == null ? Component.text("") : ChatUtil.toComponent(sub);
        world.showTitle(Title.title(titleComp, subComp, Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(1000), Duration.ofMillis(500))));
    }

    public void broadcastSound(SoundEvent sound) {
        world.playSound(Sound.sound(sound, Sound.Source.PLAYER, 0.2f, 1f));
    }

    private void changeState(UhcState state) {
        this.state.obj().stop();
        this.state = state;
        this.state.obj().start(this);
    }

    public void startGame() {
        Timer.countDown(1, i -> broadcast("&aThe game will start in &6" + i + "&a seconds!")).thenAccept(v -> internalStartGame());
    }

    private void internalStartGame() {
        changeState(UhcState.IN_GAME);
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

    private void spectatorInit(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void playerJoin(Player player) {
        switch (state) {
            case PREGAME -> {
                playerInit(player);

                if (players.size() == config.minPlayers) {
                    startGame();
                }
            }

            case IN_GAME -> {
                if (!config().lateJoins) {
                    player.sendMessage(ChatUtil.toComponent("&cThe game has already started so you have been made a spectator"));
                    spectatorInit(player);
                    break;
                }
                playerInit(player);
            }

            default -> {  // Spec
                player.sendMessage(ChatUtil.toComponent("&cThe game has already started so you have been made a spectator"));
                spectatorInit(player);
            }
        }
    }

    /**
     * This future is called just before the server shuts down after the game is finished.
     * This will only ever be called once.
     */
    public CompletableFuture<Void> preShutdown() {
        return preShutdown;
    }

    public UhcStatus status() {
        return status;
    }

    public UhcState state() {
        return state;
    }

    public long gameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public List<Player> players() {
        return players;
    }

    public void triggerEvent(UhcEvent event) {
        event.start(this);
        events.remove(event);
    }

    public UhcEvent getNextEvent() {
        UhcEvent soonest = null;
        for (UhcEvent event : events) {
            if (soonest == null) {
                soonest = event;
                continue;
            }

            if (soonest.startTime().toMillis() > event.startTime().toMillis()) {
                soonest = event;
            }
        }

        return soonest;
    }

    /**
     * Configuration for a UHC game.
     * @param worldRadius The world border radius and area which player can be spawned in.
     * @param minPlayers The minimum players required before the game can start.
     * @param lateJoins Allow players to join after the game has started.
     * @param events Events that will happen at specific times.
     */
    public record Config(int worldRadius, int minPlayers, boolean lateJoins, String ip, List<UhcEvent> events) { }
}
