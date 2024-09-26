package com.lostmc.core.punishment;

import com.lostmc.core.punishment.interfaces.Expirable;

public class SilentPunishment extends Punishment implements Expirable {

    private final Category category;
    private final long expiresIn;

    public SilentPunishment(String id, String reason, String serverId, PlayerData playerData, PlayerData actorData, Category category,
                            long createdIn, long expiresIn) {
        super(id, reason, serverId, playerData, actorData, createdIn, Type.SILENT);
        this.category = category;
        this.expiresIn = expiresIn;
    }

    @Override
    public long getExpiresIn() {
        return expiresIn;
    }
}
