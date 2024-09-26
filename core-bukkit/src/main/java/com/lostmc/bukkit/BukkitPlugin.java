package com.lostmc.bukkit;

import com.comphenix.protocol.ProtocolConfig;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.lostmc.bukkit.api.cooldown.CooldownAPI;
import com.lostmc.bukkit.api.item.ActionItemStack;
import com.lostmc.bukkit.api.menu.MenuClickListener;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.command.CommandLoader;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.leaderboard.HologramTopController;
import com.lostmc.bukkit.leaderboard.NpcTopController;
import com.lostmc.bukkit.listener.ListenerLoader;
import com.lostmc.bukkit.permission.BukkitPermissionManager;
import com.lostmc.bukkit.pubsub.BukkitPubSub;
import com.lostmc.bukkit.server.BukkitProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.CommonsPlatform;
import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.backend.redis.RedisBackend;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.permission.PermissionManager;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.translate.Translator;
import com.lostmc.core.utils.JsonBuilder;
import com.viaversion.viaversion.api.Via;
import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.PaperSpigotConfig;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

public abstract class BukkitPlugin extends JavaPlugin implements CommonsPlatform, Listener {

    // PLUGIN
    @Getter
    @Setter
    private static BukkitPlugin instance;
    // CONTROLLER
    @Getter
    private static Control control;
    // PERMISSION MANAGER
    @Getter
    private PermissionManager<Player> permissionManager;
    // COLOURED CONSOLE COMMAND SENDER
    @Setter
    @Getter
    private static CommandSender console;
    // PACKET LISTENER
    private final PacketListenerAPI packetListener = new PacketListenerAPI();
    // LISTENER LOADER
    @Getter
    private ListenerLoader listenerLoader;
    // COMMAND LOADER
    @Getter
    private CommandLoader commandLoader;
    // HOLOGRAM

    public BukkitPlugin() {
        setConsole(Bukkit.getConsoleSender());
        BukkitPlugin.instance = this;

        SpigotConfig.restartOnCrash = true;
        SpigotConfig.restartMessage = "§creiniciando, voltamos já!";
        SpigotConfig.outdatedClientMessage = "§cServidor disponível apenas para 1.7 á 1.18";
        SpigotConfig.outdatedServerMessage = "§cServidor disponível apenas para 1.7 á 1.18";
        SpigotConfig.disableStatSaving = true;
        SpigotConfig.serverFullMessage = "§cServidor lotado!";
        SpigotConfig.whitelistMessage = "§cestamos em manutenção, voltamos já!";
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        Commons.initialize(this);
        defineBackends();

        // VIA VERSION
        Via.getConfig().setCheckForUpdates(false);

        // PROTOCOLLIB
        try {
            Method method = ProtocolLibrary.class.getMethod("getConfiguration");
            ProtocolConfig protocolConfig = (ProtocolConfig) method.invoke(null);
            protocolConfig.setAutoNotify(false);
            protocolConfig.setAutoDownload(false);
            protocolConfig.setMetricsEnabled(false);
        } catch (Exception e) {
            ProtocolConfig protocolConfig = ProtocolLibrary.getConfig();
            protocolConfig.setAutoNotify(false);
            protocolConfig.setAutoDownload(false);
            protocolConfig.setMetricsEnabled(false);
        }

        String serverId = getConfig().getString("server.id", "UNDEFINED-A1");
        ServerType serverType;
        try {
            serverType = ServerType.valueOf(getConfig().getString("server.type"));
        } catch (Exception e) {
            e.printStackTrace();
            serverType = ServerType.UNDEFINED;
        }

        int maxPlayers = getServer().getMaxPlayers();
        boolean whitelisted = getServer().hasWhitelist();

        Commons.setProxyHandler(new BukkitProxyHandler(new ProxiedServer(serverId, serverType,
                new HashMap<>(), maxPlayers, whitelisted)));

        this.packetListener.load();
        BukkitPlugin.control = new Control(this);
        this.listenerLoader = new ListenerLoader(getFile(), this);
        this.commandLoader = new CommandLoader(getFile());
    }

    @Override
    public void onEnable() {
        Translator.loadTranslations();

        boolean startupError = false;
        try {
            Commons.getMysqlBackend().connect();
            Commons.getRedisBackend().connect();
            Commons.getRedisBackend().registerPubSub(new BukkitPubSub(), PacketType.toChannelsArray());
        } catch (Throwable ex) {
            startupError = true;
            ex.printStackTrace();
        } finally {
            Commons.getStorageCommon().initialize();

            packetListener.init(this);
            BukkitPlugin.control.onEnable();
            permissionManager = new BukkitPermissionManager(this);

            /**
             * Cancelling entity death animation
             */
            PacketListenerAPI.addPacketHandler(new PacketHandler(this) {
                @Override
                public void onSend(SentPacket packet) {
                    if (packet.getPacketName().equals("PacketPlayOutEntityStatus")) {
                        if (((byte) packet.getPacketValue("b")) == 3) {
                            packet.setCancelled(true);
                        }
                    }
                }

                @Override
                public void onReceive(ReceivedPacket packet) {

                }
            });

            ScoreboardHandler.getInstance().registerTimer(this);

            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

            PluginManager manager = getServer().getPluginManager();

            manager.registerEvents(this, this);
            manager.registerEvents(new CooldownAPI(), this);
            manager.registerEvents(new ActionItemStack(), this);
            manager.registerEvents(new MenuClickListener(), this);

            listenerLoader.loadListeners("com.lostmc.bukkit.listener.registry");
            commandLoader.loadCommands("com.lostmc.bukkit.command.registry");

            getServer().getScheduler().runTaskLater(this, () -> unregisterCommands("say", "me", "pl", "plugins", "icanhasbukkit", "ver", "version", "?", "help", "viaversion",
                    "viaver", "vvbukkit", "holograms", "hd", "holo", "hologram", "protocol", "packet", "filter", "packetlog"), 2L);

            new BukkitRunnable() {

                private long tick;

                @Override
                public void run() {
                    if (++tick % 30 == 0) {
                        getServer().getOnlinePlayers().stream().filter(p ->
                                        Profile.fromUniqueId(p.getUniqueId()) == null)
                                .forEach(p -> p.kickPlayer("§cERR_PROFILE_UNLOADED"));
                    }
                    getServer().getPluginManager().callEvent(new ServerTimerEvent(tick));
                }
            }.runTaskTimer(this, 1L, 1L);

            if (!startupError) {
                Commons.getPlatform().runAsync(() -> {
                    Commons.getProxyHandler().startLocal();
                    if (Commons.getProxyHandler().getLocal().getServerType() != ServerType.HUNGERGAMES) {
                        Commons.setSystemReady(true);
                        getLogger().info("System ready to accept players");
                    }
                });
            } else {
                getLogger().info("Startup error! Check console for logs and restart the server");
            }
        }
    }

    @Override
    public void onDisable() {
        for (Player ps : getServer().getOnlinePlayers())
            ps.kickPlayer("§creiniciando, voltamos já!");
        Commons.setSystemReady(false);
        Commons.getProxyHandler().stopLocal();
        try {
            Commons.getMysqlBackend().disconnect();
            Commons.getRedisBackend().disconnect();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.packetListener.disable(this);
    }

    private void defineBackends() {
        HikariConfig databaseConfig = new HikariConfig();
        databaseConfig.setDriverClassName("com.mysql.jdbc.Driver");

        String database = getConfig().getString("backend.mysql.database", "core");

        databaseConfig.setConnectionTimeout(getConfig().getInt("backend.mysql.timeout", 30) * 1_000L);
        databaseConfig.setMaxLifetime(getConfig().getInt("backend.mysql.lifetime", 30) * 1_000L);
        String host = getConfig().getString("backend.mysql.host", "localhost");
        int port = getConfig().getInt("backend.mysql.port", 3306);
        boolean useSSL = getConfig().getBoolean("backend.mysql.useSSL", false);

        if (useSSL) {
            databaseConfig.addDataSourceProperty("allowPublicKeyRetrieval",
                    getConfig().getBoolean("backend.mysql.allowPublicKeyRetrieval", false));
            databaseConfig.addDataSourceProperty("serverRSAPublicKeyFile",
                    getConfig().getString("backend.mysql.ServerRSAPublicKeyFile"));
            databaseConfig.addDataSourceProperty("sslMode",
                    getConfig().getString("backend.mysql.sslMode", "Required"));
        }

        databaseConfig.setUsername(getConfig().getString("backend.mysql.username", "root"));
        databaseConfig.setPassword(getConfig().getString("backend.mysql.password", ""));

        String jdbcURL = "mysql" + "://" + host + ':' + port + '/' + database;

        MySQLBackend backend = new MySQLBackend(jdbcURL, databaseConfig,
                host, port, database, useSSL);

        Commons.setMysqlBackend(backend);
        Commons.getStorageCommon().initializeParams(backend);

        host = getConfig().getString("backend.redis.host", "localhost");
        port = getConfig().getInt("backend.redis.port", 6379);

        Commons.setRedisBackend(new RedisBackend(host,
                getConfig().getString("backend.redis.password", ""), port));
    }

    @SuppressWarnings("unchecked")
    public void unregisterCommands(String... commands) {
        try {
            Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f1.setAccessible(true);
            CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
            Field f2 = commandMap.getClass().getDeclaredField("knownCommands");
            f2.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);
            for (String command : commands) {
                if (knownCommands.containsKey(command)) {
                    knownCommands.remove(command);
                    List<String> aliases = new ArrayList<>();
                    for (String key : knownCommands.keySet()) {
                        if (!key.contains(":")) continue;
                        String substr = key.substring(key.indexOf(":") + 1);
                        if (substr.equalsIgnoreCase(command)) {
                            aliases.add(key);
                        }
                    }
                    for (String alias : aliases) {
                        knownCommands.remove(alias);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onWorld(WorldLoadEvent event) {
        if (!getControl().getControllers().containsKey(NpcTopController.class)) {
            getControl().enableController(NpcTopController.class);
        }
        if (!getControl().getControllers().containsKey(HologramTopController.class)) {
            getControl().enableController(HologramTopController.class);
        }
    }

    @Override
    public Path getPath() {
        return this.getDataFolder().toPath();
    }

    @Override
    public boolean isPluginInstalled(String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @Override
    public String getNameOf(UUID uuid) {
        Player of = getServer().getPlayer(uuid);
        if (of != null)
            return of.getName();
        return null;
    }

    @Override
    public UUID getUUIDOf(String name) {
        Player of = getServer().getPlayer(name);
        if (of != null)
            return of.getUniqueId();
        return null;
    }

    @Override
    public void runAsync(Runnable command) {
        getServer().getScheduler().runTaskAsynchronously(this, command);
    }

    @Override
    public void updateData(UUID id, DataType dataType, Object data) {
        Collection<? extends Player> players = getServer().getOnlinePlayers();

        if (players.size() > 0) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("UpdateSingleData");
            out.writeUTF(id.toString());
            out.writeUTF(dataType.toString());
            out.writeUTF(new JsonBuilder().addElement("data", Commons.getGson().toJsonTree(data))
                    .build().toString());
            players.iterator().next().sendPluginMessage(this, "BungeeCord", out.toByteArray());
        } else {
            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                    new PacketUpdateSingleData(id, dataType, Commons.getGson().toJsonTree(data)).toJson());
        }
    }

    public abstract void onProfileLoad(Profile profile);

    public abstract void onProfileUnload(Profile profile);
}
