package com.lostmc.core.punishment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPBanPunishment extends BanPunishment {

    private String ipAddress;

    public IPBanPunishment(String id, String reason, String serverId, PlayerData playerData, PlayerData actorData,
                           Category category, long createdIn, long expiresIn, boolean pardonnable, boolean antiCheatBan, String ipAddress) {
        super(id, reason, serverId, playerData, actorData, Type.IP_BAN, category, createdIn, expiresIn, antiCheatBan,
                pardonnable);
        this.ipAddress = ipAddress;
    }
}
