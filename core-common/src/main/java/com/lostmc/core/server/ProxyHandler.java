package com.lostmc.core.server;

import com.lostmc.core.server.minigame.HungerGamesServer;
import com.lostmc.core.utils.JsonUtils;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketServerInfo;
import com.lostmc.core.networking.PacketType;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class ProxyHandler {

    @Setter
    private ProxiedServer local;

    public ProxyHandler(final ProxiedServer local) {
        this.local = local;
    }

    public void updateLocal() {
        this.local.update();
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.hmset("server:" + this.local.getId(), JsonUtils.objectToMap(this.local));
            jedis.publish(PacketType.SERVER_INFO_UPDATING.toString(), Commons.getGson().toJson(
                    new PacketServerInfo(this.local.getServerType(), this.local)));
        }
    }

    public void stopLocal() {
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.srem("server:type:" + this.local.getServerType().toString().toLowerCase(), this.local.getId());
            jedis.del("server:" + this.local.getId());
            jedis.publish(PacketType.SERVER_INFO_STOPPING.toString(),
                    Commons.getGson().toJson(new PacketServerInfo(this.local.getServerType(), this.local)));
        }
    }

    public void startLocal() {
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.sadd("server:type:" + this.local.getServerType().toString().toLowerCase(), this.local.getId());
            jedis.hmset("server:" + this.local.getId(), JsonUtils.objectToMap(this.local));
            jedis.publish(PacketType.SERVER_INFO_STARTING.toString(), new PacketServerInfo(
                    this.local.getServerType(), this.local).toJson());
        }
    }

    public ProxiedServer getServer(String serverId) {
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            if (!jedis.exists("server:" + serverId))
                return null;
            Map<String, String> map = jedis.hgetAll("server:" + serverId);
            return getInstance(map);
        }
    }

    public Set<ProxiedServer> getServers(ServerType serverType) {
        Set<ProxiedServer> servers = new HashSet<>();
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            for (String server : jedis.sunion("server:type:" + serverType.toString().toLowerCase())) {
                Map<String, String> map = jedis.hgetAll("server:" + server);
                ProxiedServer n = getInstance(map);
                servers.add(n);
            }
        }
        return servers;
    }

    public Set<ProxiedServer> getAllServers() {
        Set<ProxiedServer> servers = new HashSet<>();
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            String[] str = new String[ServerType.values().length];
            for (int i = 0; i < ServerType.values().length; i++) {
                str[i] = "server:type:" + ServerType.values()[i].toString().toLowerCase();
            }
            for (String server : jedis.sunion(str)) {
                Map<String, String> map = jedis.hgetAll("server:" + server);
                ProxiedServer n = getInstance(map);
                servers.add(n);
            }
        }
        return servers;
    }

    public static ProxiedServer getInstance(Map<String, String> map) {
        switch (ServerType.valueOf(map.get("serverType").replace("\"", ""))) {
            case HUNGERGAMES: {
                return JsonUtils.mapToObject(map, HungerGamesServer.class);
            }
            default: {
                return JsonUtils.mapToObject(map, ProxiedServer.class);
            }
        }
    }
}
