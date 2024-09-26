package com.lostmc.core.punishment.provider;

import com.lostmc.core.Commons;
import com.lostmc.core.punishment.BanPunishment;
import com.lostmc.core.punishment.IPBanPunishment;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.SilentPunishment;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class PunishmentProvider {

    public static Punishment provideNewBaniment(String reason, String serverId, Punishment.PlayerData playerData, Punishment.PlayerData actorData,
                                                Punishment.Category category, long expiresIn, boolean antiCheatBan,
                                              boolean pardonnable) throws PunishmentIDProvideException {
        return new BanPunishment(provideNewId(), reason, serverId, playerData, actorData, category, System.currentTimeMillis(),
                expiresIn, antiCheatBan, pardonnable);
    }

    public static Punishment provideNewIPBaniment(String reason, String serverId, Punishment.PlayerData playerData, Punishment.PlayerData actorData,
                                                  Punishment.Category category, long expiresIn, boolean antiCheatBan,
                                                  boolean pardonnable, String ipAddress) throws PunishmentIDProvideException {
        return new IPBanPunishment(provideNewId(), reason, serverId, playerData, actorData, category, System.currentTimeMillis(),
                expiresIn, antiCheatBan, pardonnable, ipAddress);
    }

    public static Punishment provideNewSilent(String reason, String serverId, Punishment.PlayerData playerData, Punishment.PlayerData actorData, Punishment.Category category,
                                              long expiresIn) throws PunishmentIDProvideException {
        return new SilentPunishment(provideNewId(), reason, serverId, playerData, actorData, category, System.currentTimeMillis(), expiresIn);
    }

    private static String provideNewId() throws PunishmentIDProvideException {
        try {
            int count = 0;
            String id;
            while (Commons.getStorageCommon().getPunishmentStorage().getPunishment((
                    id = UUID.randomUUID().toString().substring(0, 8))) != null) {
                if (++count >= 50) {
                    throw new PunishmentIDProvideException("Took too long to provide id");
                }
            }
            return id;
        } catch (Exception e) {
            throw new PunishmentIDProvideException(e);
        }
    }

    public static BaseComponent[] provideBanimentKickMessage(BanPunishment baniment) {
        StringBuilder sb = new StringBuilder();
        sb.append("§cVocê está banido " + (baniment.isLifetime() ? "permanentemente" : "temporariamente"))
                .append("\n");
        sb.append("§cMotivo: " + baniment.getCategory().getDefaultReason()).append("\n");
        if (baniment.isAntiCheatBan())
            sb.append("§cBanimento automático.").append("\n");
        if (!baniment.isLifetime())
            sb.append("§cExpira em: " + DateUtils.getTime(baniment.getExpiresIn())).append("\n");
        sb.append("§cPode comprar unban: " + (baniment.isPardonnable() ? "Sim" : "Não")).append("\n");
        sb.append("§cID: #" + baniment.getId()).append("\n");
        sb.append("\n");
        sb.append("§cBanido injustamente? Peça appeal em discord.gg/lostmc");
        if (baniment.isPardonnable())
            sb.append("\n").append("§cAdquira seu unban em loja.lostmc.com.br");
        return TextComponent.fromLegacyText(sb.toString());
    }
}
