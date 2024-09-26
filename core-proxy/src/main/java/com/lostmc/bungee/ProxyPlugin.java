package com.lostmc.bungee;

import club.minnced.discord.webhook.WebhookClient;
import com.lostmc.bungee.manager.LoginManager;
import com.lostmc.bungee.manager.SilentManager;
import com.lostmc.bungee.permission.ProxyPermissionManager;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.manager.MotdManager;
import com.lostmc.bungee.pubsub.ProxyPubSub;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.CommonsPlatform;
import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.backend.redis.RedisBackend;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.permission.PermissionManager;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.translate.Translator;
import com.lostmc.core.utils.ClassGetter;
import com.google.common.io.ByteStreams;
import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProxyPlugin extends Plugin implements CommonsPlatform {

    private static ProxyPlugin instance;

    @Getter
    private PermissionManager<ProxiedPlayer> permissionManager;
    @Getter
    private Configuration config;

    @Getter
    private MotdManager motdManager;
    @Getter
    private boolean maintenance;
    private List<String> whitelist;

    @Getter
    private LoginManager loginManager;
    @Getter
    private SilentManager silentManager;

    @Getter
    private WebhookClient staffChatWebHook;
    @Getter
    private WebhookClient reportsWebHook;
    @Getter
    private WebhookClient bansWebHook;

    private List<String> messages;
    private int nextMessageId = 0;

    public ProxyPlugin() {
        ProxyPlugin.instance = this;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        Commons.initialize(this);
        defineBackends();

        ProxiedServer server;

        String serverId = getConfig().getString("server.id", "PROXY");
        ServerType serverType;
        try {
            serverType = ServerType.valueOf(getConfig().getString("server.type"));
        } catch (Exception e) {
            serverType = ServerType.PROXY;
        }

        int maxPlayers = getProxy().getConfig().getPlayerLimit();
        // TODO: check custom whitelist
        boolean whitelisted = false;

        Commons.setProxyHandler(new BungeeProxyHandler(getProxy(), new ProxiedServer(serverId, serverType,
                new HashMap<>(), maxPlayers, whitelisted)));


        {
            messages = new ArrayList<>();

            messages.add("&fAdquira &6ranks&f, &amedalhas&f e &cpermissões&f em &9loja.lostmc.com.br");
            messages.add("&fEncontrou um &chacker&f? Utilize &d/report <jogador> <motivo>");
            messages.add("&fUtilize &e/play&f e escolha um &9modo de jogo&f para jogar!");

            getConfig().set("server.broadcast-messages", messages);
            saveConfig();
        }

        maintenance = getConfig().getBoolean("server.maintenance", false);
        whitelist = getConfig().getStringList("server.whitelist");
    }

    @Override
    public void onEnable() {
        Translator.loadTranslations();

        boolean startupError = false;
        try {
            Commons.getMysqlBackend().connect();
            Commons.getRedisBackend().connect();
            Commons.getRedisBackend().registerPubSub(new ProxyPubSub(), PacketType.toChannelsArray());
        } catch (Throwable e) {
            e.printStackTrace();
            startupError = true;
        }

        if (!startupError) {
            Commons.getStorageCommon().initialize();
        }

        try {
            staffChatWebHook = WebhookClient.withId(988541830788419634L,
                    "27Cgteu8LuGUCAMZ9X8pSB67nHq66YlhcawFPpB-hRWqsqF5DeI_awwq00mL0rHmybbP");
        } catch (Exception e) {
            getLogger().info("Falha ao iniciar o web hook do staff chat: " + e);
        }

        this.permissionManager = new ProxyPermissionManager(this);

        (this.motdManager = new MotdManager(this)).onEnable();
        (this.loginManager = new LoginManager(this)).onEnable();
        (this.silentManager = new SilentManager(this)).onEnable();

        for (Class<?> c : ClassGetter.getClassesForPackageByFile(getFile(), "com.lostmc." +
                "bungee.listener.registry")) {
            if (!ProxyListener.class.isAssignableFrom(c))
                continue;
            try {
                ((ProxyListener) c.getConstructor().newInstance()).register(this);
                getLogger().info("Listener '" + c.getSimpleName() + "' registered.");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        PluginManager manager = getProxy().getPluginManager();
        for (Plugin plugin : manager.getPlugins()) {
            manager.unregisterCommands(plugin);
        }

        for (Class<?> c : ClassGetter.getClassesForPackageByFile(getFile(), "com.lostmc." +
                "bungee.command.registry")) {
            if (!WrappedProxyCommand.class.isAssignableFrom(c))
                continue;
            try {
                ((WrappedProxyCommand) c.getConstructor().newInstance()).register(getProxy().getPluginManager());
                getLogger().info("Command '" + c.getSimpleName() + "' registered.");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (!startupError) {
            runAsync(() -> {
                Commons.getProxyHandler().startLocal();
                Commons.setSystemReady(true);
                getLogger().info("Proxy ready to accept connections");
            });
            getProxy().getScheduler().schedule(this, () -> {
                if (!Commons.isSystemReady())
                    return;

                ProxiedServer localhost = Commons.getProxyHandler().getLocal();

                localhost.getPlayers().clear();
                for (ProxiedPlayer p : getProxy().getPlayers()) {
                    localhost.getPlayers().put(p.getUniqueId(), p.getName());
                }

                localhost.setMaxPlayers(getProxy().getConfig().getPlayerLimit());
                localhost.setStarted(true);
                localhost.setWhitelisted(false);

                Commons.getProxyHandler().setLocal(localhost);

                runAsync(() -> {
                    Commons.getProxyHandler().updateLocal();
                });
            }, 1, 1, TimeUnit.SECONDS);

            getProxy().getScheduler().schedule(this, () -> {
                if (!messages.isEmpty()) {
                    if (nextMessageId >= messages.size())
                        nextMessageId = 0;
                    getProxy().broadcast(new TextComponent("§b§lLOST §7» §f" + messages
                            .get(nextMessageId).replace('&', '§')));
                    ++nextMessageId;
                }
            }, 5, 5, TimeUnit.MINUTES);
        } else {
            getLogger().info("Startup error! Check console for logs and restart the proxy");
        }
    }

    @Override
    public void onDisable() {
        Commons.getProxyHandler().stopLocal();
        try {
            Commons.getMysqlBackend().disconnect();
            Commons.getRedisBackend().disconnect();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        Commons.setSystemReady(false);
    }

    public void setMaintenance(boolean maintenance) {
        getConfig().set("server.maintenance", this.maintenance = maintenance);
        saveConfig();
    }

    public boolean isWhitelisted(UUID uuid) {
        return whitelist.contains(uuid.toString());
    }

    public void setWhitelisted(UUID uuid, boolean b) {
        if (b) {
            if (!whitelist.contains(uuid.toString())) {
                whitelist.add(uuid.toString());
                getConfig().set("server.whitelist", whitelist);
                saveConfig();
            }
        } else if (whitelist.contains(uuid.toString())) {
            if (whitelist.contains(uuid.toString())) {
                whitelist.remove(uuid.toString());
                getConfig().set("server.whitelist", whitelist);
                saveConfig();
            }
        }
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

    private void saveDefaultConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(configFile)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create configuration file", e);
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,
                    new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProxyPlugin getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Proxy";
    }

    @Override
    public Path getPath() {
        return getDataFolder().toPath();
    }

    @Override
    public boolean isPluginInstalled(String name) {
        return getProxy().getPluginManager().getPlugin(name) != null;
    }

    @Override
    public String getNameOf(UUID uuid) {
        ProxiedPlayer of = getProxy().getPlayer(uuid);
        if (of != null)
            return of.getName();
        return null;
    }

    @Override
    public UUID getUUIDOf(String name) {
        ProxiedPlayer of = getProxy().getPlayer(name);
        if (of != null)
            return of.getUniqueId();
        return null;
    }

    @Override
    public void updateData(UUID id, DataType dataType, Object data) {

    }

    @Override
    public void runAsync(Runnable task) {
        getProxy().getScheduler().runAsync(this, task);
    }
}
