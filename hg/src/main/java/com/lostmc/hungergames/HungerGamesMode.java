package com.lostmc.hungergames;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.GameMode;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.ScheduleArgs;
import com.lostmc.hungergames.event.game.GameStartEvent;
import com.lostmc.hungergames.listener.*;
import com.lostmc.hungergames.manager.*;
import com.lostmc.hungergames.scheduler.InvincibilityScheduler;
import com.lostmc.hungergames.sidebar.InvincibilitySidebarModel;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.util.MapUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class HungerGamesMode extends GameMode {

    public static int MINIMUM_PLAYERS = 5;
    public static int INVINCIBILITY_TIME = 120;
    public static int FEAST_SPAWN = (15 * 60) + 30;
    public static int BONUSFEAST_SPAWN = 30 * 60;
    public static int FINALBATTLE_TIME = 45 * 60;
    public static boolean FINISHED;

    public HungerGamesMode(GamePlugin main, boolean doubleKit) {
        super(main, doubleKit);
    }

    public void checkWinner() {
        if (((HungerGames) getMain()).playersLeft() > 1)
            return;
        Player pWin = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            HungerGamer gamer = HungerGamer.getGamer(p);
            if (gamer.isNotPlaying())
                continue;
            if (!p.isOnline())
                continue;
            pWin = p;
            break;
        }
        if (pWin == null) {
            replayAll();
            getServer().getScheduler().runTaskLater(getMain(), () -> {
                Bukkit.broadcastMessage("§cNão houve vencedores! Reiniciando servidor...");
                Bukkit.shutdown();
            }, 5
            );
            return;
        }
        ((HungerGames) getMain()).setGameStage(GameStage.ENDING);
        FINISHED = true;
        final Player winner = pWin;
        Profile profile = Profile.getProfile(winner);
        HungerGamer win = HungerGamer.getGamer(winner);
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.broadcastMessage("§c" + winner.getName() + " venceu o torneio!");
            }
        }.runTaskTimer(getMain(), 20, 20);

        profile.setData(DataType.HG_TOTAL_WINS, profile.getData(DataType.HG_TOTAL_WINS).getAsInt() + 1);
        if (isDoubleKit())
            profile.setData(DataType.HG_DOULEKIT_WINS, profile.getData(DataType.HG_DOULEKIT_WINS).getAsInt() + 1);
        else
            profile.setData(DataType.HG_SINGLEKIT_WINS, profile.getData(DataType.HG_SINGLEKIT_WINS).getAsInt() + 1);
        int coinsGain = 240;
        profile.setData(DataType.COINS, profile.getData(DataType.COINS).getAsInt() + coinsGain);
        winner.sendMessage("§6+" + coinsGain + " coins");
        int eloGain = 30;
        if (win.getMatchKills() > 0)
            eloGain += 2 * win.getMatchKills();
        profile.setData(DataType.HG_ELO, profile.getData(DataType.HG_ELO).getAsInt() + eloGain);
        profile.save();

        Location cake = winner.getLocation().clone();
        cake.setY(156.0D);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                cake.clone().add(x, 0.0D, z).getBlock().setType(Material.GLASS);
                cake.clone().add(x, 1.0D, z).getBlock().setType(Material.CAKE_BLOCK);
            }
        }

        winner.sendMessage("§a+" + eloGain + " ELO");
        winner.setGameMode(org.bukkit.GameMode.CREATIVE);
        winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        winner.teleport(cake.clone().add(0.0D, 4.0D, 0.0D));
        winner.getInventory().setArmorContents(null);

        winner.getWorld().setTime(13000L);

        startFirework(winner, winner.getLocation(), Commons.getRandom());

        new BukkitRunnable() {

            @Override
            public void run() {
                replayAll();
                getServer().getScheduler().runTaskLater(getMain(), () -> {
                    Bukkit.shutdown();
                }, 5);
            }
        }.runTaskLater(getMain(), 20 * 30);
    }

    public void startFirework(final Player player, Location location, Random random) {
        for (int i = 0; i < 5; i++) {
            spawnRandomFirework(location.add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
        }
        new BukkitRunnable() {
            public void run() {
                spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, -10.0D));
                spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, 10.0D));
                spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, -10.0D));
                spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, 10.0D));
                spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, -5.0D));
                spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, 5.0D));
                spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, -5.0D));
                spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, 5.0D));
                spawnRandomFirework(player.getLocation().add(-4.0D, 0.0D, -3.0D));
                spawnRandomFirework(player.getLocation().add(-3.0D, 0.0D, 4.0D));
                spawnRandomFirework(player.getLocation().add(2.0D, 0.0D, -6.0D));
                spawnRandomFirework(player.getLocation().add(1.0D, 0.0D, 9.0D));
            }
        }.runTaskTimer(getMain(), 10L, 30L);
    }

    public void spawnRandomFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();

        int rt = Commons.getRandom().nextInt(4) + 1;

        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (rt == 1) {
            type = FireworkEffect.Type.BALL;
        } else if (rt == 2) {
            type = FireworkEffect.Type.BALL_LARGE;
        } else if (rt == 3) {
            type = FireworkEffect.Type.BURST;
        } else if (rt == 4) {
            type = FireworkEffect.Type.STAR;
        }
        FireworkEffect effect = FireworkEffect.builder().flicker(Commons.getRandom().nextBoolean())
                .withColor(Color.WHITE).withColor(Color.ORANGE).withFade(Color.FUCHSIA).with(type)
                .trail(Commons.getRandom().nextBoolean()).build();
        fwm.addEffect(effect);
        fwm.setPower(Commons.getRandom().nextInt(2) + 1);

        firework.setFireworkMeta(fwm);
    }

    @Override
    public void onLoad() {
        MapUtils.deleteWorld("world");
        ((HungerGames) getMain()).setGameStage(GameStage.WAITING);
        ((HungerGames) getMain()).setMinimumPlayers(MINIMUM_PLAYERS);
    }

    public void replayAll() {
        for (Player ps : getServer().getOnlinePlayers()) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("REPLAY_HGMIX");
            ps.sendPluginMessage(getMain(), "BungeeCord", output.toByteArray());
        }
    }

    @Override
    public void onEnable() {
        Management.enableManagement(AssistManager.class);
        Management.enableManagement(GamerManager.class);
        Management.enableManagement(KitManager.class);
        Management.enableManagement(SchedulerManager.class);
        Management.enableManagement(MatchManager.class);
        loadListeners();
        getMain().getCommandLoader().loadCommands("com.lostmc.hungergames.command");
        boolean doubleKit = getMain().getConfig().getBoolean("game.hg.double-kit", true);
        setDoubleKit(doubleKit);
        getServer().getScheduler().runTaskAsynchronously(getMain(), () -> {
            getMain().getConfig().set("game.hg.double-kit", !doubleKit);
            getMain().saveConfig();
        });
        getMain().getServer().getScheduler().scheduleSyncDelayedTask(getMain(), () -> {
            World world = getServer().getWorld("world");
            world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
            for (int x = -5; x <= 5; x++)
                for (int z = -5; z <= 5; z++)
                    world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
            world.setDifficulty(Difficulty.EASY);
            if (world.hasStorm())
                world.setStorm(false);
            world.setWeatherDuration(1000000000);
            world.setGameRuleValue("doDaylightCycle", "false");
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(1000);
            for (Entity e : world.getEntities()) {
                e.remove();
            }
        });
    }

    @Override
    public void onDisable() {

    }

    private void loadListeners() {
        getMain().getServer().getPluginManager().registerEvents(new BlockListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new AchievementListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new KitListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new EventListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new GameStageChangeListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new InitialSpawnListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new PregameListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new InventoryListener(),
                getMain());
        getMain().getServer().getPluginManager().registerEvents(new BorderListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new ScoreboardListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new ServerInfoUpdateListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new SpectatorListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new VanishListener(), getMain());
    }

    @Override
    public void startGame() {
        getServer().getPluginManager().callEvent(new GameStartEvent());
        Bukkit.broadcastMessage("§cO torneio iniciou! Boa sorte a todos!");
        getMain().getServer().getPluginManager().registerEvents(new GameListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new CombatLogListener(), getMain());
        getMain().getServer().getPluginManager().registerEvents(new DeathListener(),
                getMain());
        InvincibilityScheduler scheduler = new InvincibilityScheduler();
        scheduler.pulse(
                new ScheduleArgs(getMain().getGameType(), ((HungerGames) getMain()).getGameStage(),
                        ((HungerGames) getMain()).getTimer()));
        Management.getManagement(SchedulerManager.class).addScheduler("invincibility", scheduler);
        ((HungerGames) getMain()).setTimer(INVINCIBILITY_TIME);
        ((HungerGames) getMain()).setGameStage(GameStage.INVINCIBILITY);
        getMain().getServer().getPluginManager().registerEvents(new InvincibilityListener(),
                getMain());
        for (Player p : getMain().getServer().getOnlinePlayers()) {
            HungerGamer gamer = HungerGamer.getGamer(p);
            Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard(p);
            scoreboard.setModel(new InvincibilitySidebarModel(scoreboard));
            if (gamer.isNotPlaying())
                continue;
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
            p.closeInventory();
            p.getInventory().clear();
            p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            for (Kit playerKit : gamer.getKits().values()) {
                if (playerKit.getItems() != null) {
                    p.getInventory().addItem(playerKit.getItems());
                }
            }
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
        }
    }

    @Override
    public CombatLog getCombatLog(Player p) {
        HungerGamer gamer = HungerGamer.getGamer(p);
        if (gamer == null)
            return null;
        return gamer.getCombatLog();
    }

}
