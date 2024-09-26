package com.lostmc.core.server;

import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketSetPlayersCapacity;
import com.lostmc.core.networking.PacketType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProxiedServer {

    @NonNull
    private String id;
    @NonNull
    @Setter
    private ServerType serverType;
    @NonNull
    private HashMap<UUID, String> players;
    @NonNull
    @Setter
    private int maxPlayers;
    @NonNull
    @Setter
    private boolean whitelisted;
    @Setter
    private boolean started = false;
    private long lastUpdate;

    public void update() {
        this.lastUpdate = System.currentTimeMillis();
    }

    public boolean isOutOfDate() {
        return this.lastUpdate + 6000L < System.currentTimeMillis();
    }

    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        if (this.serverType == ServerType.PROXY) {
            Commons.getRedisBackend().publish(PacketType.SET_IN_PLAYERS_CAPACITY.toString(),
                    Commons.getGson().toJson(new PacketSetPlayersCapacity(this.id, maxPlayers)));
        } else {
            Commons.getRedisBackend().publish(PacketType.SET_OUT_PLAYERS_CAPACITY.toString(),
                    Commons.getGson().toJson(new PacketSetPlayersCapacity(this.id, maxPlayers)));
        }
    }

    public boolean canBeSelected() {
        return started && !isFull() && !whitelisted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof ProxiedServer) {
            ProxiedServer that = (ProxiedServer) o;
            return that.id.equals(this.id);
        }
        return false;
    }
}
