package com.lostmc.core.server.minigame;

import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public abstract class MinigameServer extends ProxiedServer {

    @Setter
    private int time;
    @Setter
    private MinigameState state;

    public MinigameServer(@NonNull String id, @NonNull ServerType serverType, @NonNull HashMap<UUID, String> players,
                          @NonNull int maxPlayers, @NonNull boolean whitelisted) {
        super(id, serverType, players, maxPlayers, whitelisted);
    }

    public abstract boolean isInProgress();
}
