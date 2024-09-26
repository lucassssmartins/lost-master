package com.lostmc.core.punishment;

import com.lostmc.core.punishment.interfaces.Expirable;
import com.lostmc.core.punishment.interfaces.Pardonnable;
import lombok.Getter;

@Getter
public class BanPunishment extends Punishment implements Expirable, Pardonnable {

    private final Category category;
    private final long expiresIn;
    private final boolean antiCheatBan;
    private final boolean pardonnable;

    public BanPunishment(String id, String reason, String serverId, PlayerData playerData, PlayerData actorData, Type type,
                         Category category, long createdIn, long expiresIn, boolean antiCheatBan, boolean pardonnable) {
        super(id, reason, serverId, playerData, actorData, createdIn, type);
        this.category = category;
        this.expiresIn = expiresIn;
        this.antiCheatBan = antiCheatBan;
        this.pardonnable = pardonnable;
    }

    public BanPunishment(String id, String reason, String serverId, PlayerData playerData, PlayerData actorData, Category category,
                         long createdIn, long expiresIn, boolean antiCheatBan, boolean pardonnable) {
        this(id, reason, serverId, playerData, actorData, Type.BAN, category, createdIn, expiresIn, antiCheatBan, pardonnable);
    }

    @Override
    public long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public boolean isPardonnable() {
        return pardonnable;
    }
}
