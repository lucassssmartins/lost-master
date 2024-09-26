package com.lostmc.core.server.minigame;

import com.lostmc.core.server.ServerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class HungerGamesServer extends MinigameServer {

    @Getter
    @Setter
    private boolean doubleKit = false;

    public HungerGamesServer(@NonNull String id, @NonNull HashMap<UUID, String> players,
                             @NonNull int maxPlayers, @NonNull boolean whitelisted) {
        super(id, ServerType.HUNGERGAMES, players, maxPlayers, whitelisted);
        setState(MinigameState.WAITING);
    }

    @Override
    public boolean canBeSelected() {
        return super.canBeSelected() && !isInProgress() && ((getState() == MinigameState.PREGAME ||
                getState() == MinigameState.STARTING || getState() == MinigameState.WAITING) && getTime() >= 5);
    }

    @Override
    public boolean isInProgress() {
        return getState() == MinigameState.INGAME || getState() == MinigameState.GAMETIME ||
                getState() == MinigameState.INVINCIBILITY || getState() == MinigameState.ENDING;
    }
}
