package com.lostmc.hungergames;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.server.ServerType;
import com.lostmc.game.GameMode;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.GameType;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.event.game.GameStageChangeEvent;
import com.lostmc.hungergames.event.game.GameTimerEvent;
import com.lostmc.hungergames.kit.registry.controllers.GladiatorFightController;
import com.lostmc.hungergames.manager.FeastManager;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.stage.TimerType;
import com.lostmc.hungergames.util.ItemManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class HungerGames extends GamePlugin {

    public static File PLUGIN_FILE;
    public static Location spawnLocation;

    private boolean finishGettingChunks = false;
    private List<Chunk> loadedChunks = new ArrayList<>();

    private int timer;
    @Setter
    private TimerType timerType = TimerType.STOP;
    @Setter
    private int minimumPlayers = 5;
    private GameStage gameStage = GameStage.NONE;
    @Getter
    private GladiatorFightController gladiatorFightController = new GladiatorFightController();
    @Getter
    private ItemManager itemManager = new ItemManager();

    public HungerGames() {
        super();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        PLUGIN_FILE = getFile();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public GameType getDefaultGameType() {
        return GameType.HG;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (Management.getManagement(FeastManager.class) == null) {
            Management.enableManagement(FeastManager.class);
            loadChunks();
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (hasChunk(chunk)) {
            event.setCancelled(true);
        }
    }

    public void loadChunks() {
        World world = Bukkit.getWorld("world");
        for (int x = -532; x <= 532; x += 16) {
            for (int z = -532; z <= 532; z += 16) {
                loadedChunks.add(world.getBlockAt(x, 64, z).getChunk());
            }
        }
        getLogger().info("Chunks storaged! Loading...");
        Queue<Chunk> queue = new ConcurrentLinkedQueue<>(loadedChunks);
        int total = queue.size();
        int progress = 1;
        while (!queue.isEmpty()) {
            queue.poll().load(true);
            getLogger().info("Loaded " + progress + "/" + total + " chunks...");
            ++progress;
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (queue.isEmpty()) {
                    for (Chunk chunk : loadedChunks) {
                        if (!chunk.isLoaded())
                            return;
                    }
                    getLogger().info("Chunks fully loaded!");
                    getServer().getScheduler().runTaskLater(HungerGames.this, () -> {
                        Commons.setSystemReady(true);
                    }, 1200);
                    cancel();
                }
            }
        }.runTaskTimer(this, 20, 20);
    }

    public boolean hasChunk(Chunk chunk) {
        return loadedChunks.stream().filter(e -> e.getZ() == chunk.getZ() && e.getX()
                == chunk.getX()).findFirst().orElse(null) != null;
    }

    public void setGameStage(GameStage newGameStage) {
        if (newGameStage != null && newGameStage != this.gameStage) {
            GameStageChangeEvent event = new GameStageChangeEvent(this.gameStage, newGameStage);
            getServer().getPluginManager().callEvent(event);
            this.gameStage = event.getNewStage();
            setTimerType(this.gameStage.getDefaultType());
            setTimer(this.gameStage.getDefaultTimer());
        }
    }

    public void setTimer(int timer) {
        getServer().getPluginManager().callEvent(new GameTimerEvent());
        this.timer = timer;
    }

    public void count() {
        switch (timerType) {
            case COUNTDOWN:
                setTimer(timer - 1);
                break;
            case COUNT_UP:
                setTimer(timer + 1);
                break;
            default:
                break;
        }
    }

    public void checkTimer() {
        if (Commons.getProxyHandler().getLocal().getServerType() != ServerType.HG_EVENT) {
            int i = minimumPlayers;
            for (Player p : Bukkit.getOnlinePlayers()) {
                HungerGamer gamer = HungerGamer.getGamer(p);
                if (gamer == null || gamer.isNotPlaying())
                    continue;
                i--;
            }
            if (getGameStage() == GameStage.WAITING) {
                if (i <= 0) {
                    setGameStage(GameStage.PREGAME);
                }
            } else if (getGameStage() == GameStage.PREGAME || getGameStage() == GameStage.STARTING) {
                if (i > 0) {
                    setGameStage(GameStage.WAITING);
                    setTimer(GameStage.PREGAME.getDefaultTimer());
                }
            }
        }
    }

    @Override
    public GameMode loadGameMode() {
        return new HungerGamesMode(this, true);
    }

    public int playersLeft() {
        return Management.getManagement(GamerManager.class).getAliveGamers().size();
    }

    public static void teleportToSpawn(Player player) {
        player.teleport(getSpawnLocation());
    }

    public static Location getSpawnLocation() {
        if (spawnLocation != null) {
            if (!spawnLocation.getChunk().isLoaded())
                spawnLocation.getChunk().load(true);
            return spawnLocation;
        } else {
            Random r = new Random();
            int x = r.nextInt(30);
            int z = r.nextInt(30);
            if (r.nextBoolean())
                x = -x;
            if (r.nextBoolean())
                z = -z;
            World world = Bukkit.getWorlds().get(0);
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x, y + 1, z);
            return loc;
        }
    }

    @Override
    public void onProfileLoad(Profile profile) {
        Management.getManagement(GamerManager.class).loadGamer(profile);
    }

    @Override
    public void onProfileUnload(Profile profile) {

    }
}
