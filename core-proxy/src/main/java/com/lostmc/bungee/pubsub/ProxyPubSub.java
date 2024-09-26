package com.lostmc.bungee.pubsub;

import com.google.gson.JsonElement;
import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.utils.NickAPI;
import com.lostmc.core.networking.*;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.report.Report;
import com.lostmc.core.utils.JsonUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Locale;

public class ProxyPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        PacketType type = null;
        try {
            type = PacketType.valueOf(channel);
        } catch (Exception e) {
            ProxyPlugin.getInstance().getLogger().info("Unknown package type: " + channel);
            return;
        }

        switch (type) {
            case ADD_RANK:
                break;
            case REMOVE_RANK:
                break;
            case EXPIRED_RANK: {
                PacketInExpiredRank packet = JsonUtils.jsonToObject(message, PacketInExpiredRank.class);
                ProxiedPlayer player = BungeeCord.getInstance().getPlayer(packet.getPlayerUniqueId());
                if (player != null) {
                    Profile profile = Profile.getProfile(player);
                    profile.removeRank(packet.getIndex());
                    Commons.getRedisBackend().saveRedisProfile(profile);
                    ProxyPlugin.getInstance().getPermissionManager().updatePermissions(player);
                }
                break;
            }
            case UPDATE_OUT_SINGLE_DATA: {
                break;
            }
            case UPDATE_IN_SINGLE_DATA: {
                PacketUpdateSingleData pakage = JsonUtils.jsonToObject(message, PacketUpdateSingleData.class);
                Profile profile = Profile.fromUniqueId(pakage.getUniqueId());
                if (profile != null) {
                    DataType dataType = pakage.getDataType();
                    JsonElement element = pakage.getJsonElement();
                    if (dataType == DataType.NICKNAME) {
                        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(profile.getUniqueId());
                        if (p != null) {
                            String nickname = element.getAsString();
                            if (nickname.isEmpty()) {
                                NickAPI.changePlayerName(p, profile.getName());
                            } else {
                                NickAPI.changePlayerName(p, nickname);
                            }
                        }
                    }
                    profile.setDataElement(pakage.getDataType(), element);
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
                break;
            }
            case EXPIRED_IN_PERMISSION: {
                PacketInExpiredPermission packet = JsonUtils.jsonToObject(message, PacketInExpiredPermission.class);
                Profile profile = Profile.fromUniqueId(packet.getUniqueId());
                if (profile != null) {
                    try {
                        profile.getPermissions().remove(packet.getIndex());
                        Commons.getRedisBackend().saveRedisProfile(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            case UPDATE_IN_CUSTOM_PERMISSIONS: {
                PacketOutAddPermission packet = JsonUtils.jsonToObject(message, PacketOutAddPermission.class);
                Profile profile = Profile.fromUniqueId(packet.getUniqueId());
                if (profile != null) {
                    profile.addPermission(packet.getPermission());
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
                break;
            }
            case UPDATE_IN_TAG: {
                PacketInUpdateTag pakage = JsonUtils.jsonToObject(message, PacketInUpdateTag.class);
                Profile profile = Profile.fromUniqueId(pakage.getPlayerUniqueId());
                if (profile != null) {
                    profile.setTag(pakage.getTag());
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
                break;
            }
            case UPDATE_IN_MEDAL: {
                PacketInUpdateMedal pakage = JsonUtils.jsonToObject(message, PacketInUpdateMedal.class);
                Profile profile = Profile.fromUniqueId(pakage.getPlayerUniqueId());
                if (profile != null) {
                    profile.setMedal(pakage.getMedal());
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
            }
            case UPDATE_IN_LANGUAGE: {
                PacketUpdateInLanguage pakage = JsonUtils.jsonToObject(message, PacketUpdateInLanguage.class);
                Profile profile = Profile.fromUniqueId(pakage.getPlayerUniqueId());
                if (profile != null) {
                    Locale locale = pakage.getLocale();
                    if (locale != null) {
                        profile.setLocale(locale);
                        Commons.getRedisBackend().saveRedisProfile(profile);
                    } else if (profile.getLocale().equals(Commons.DEFAULT_LOCALE)) {
                        profile.setLocale(Locale.forLanguageTag("en-US"));
                        Commons.getRedisBackend().saveRedisProfile(profile);
                    } else {
                        profile.setLocale(Commons.DEFAULT_LOCALE);
                        Commons.getRedisBackend().saveRedisProfile(profile);
                    }
                }
                break;
            }
            case REPORT_REMOVE: {
                PacketReport packet = JsonUtils.jsonToObject(message, PacketReport.class);
                Report report = packet.getReport();
                Commons.getReportMap().remove(report.getPlayerUniqueId());
                break;
            }
            case UPDATE_IN_MULTIPLE_DATAS: {
                PacketUpdateMultipleDatas packet = JsonUtils.jsonToObject(message, PacketUpdateMultipleDatas.class);
                List<DataType> dataTypes = packet.getDataTypes();
                List<JsonElement> jsonElements = packet.getJsonElements();
                Profile profile = Profile.fromUniqueId(packet.getUniqueId());
                if (profile != null) {
                    for (int i = 0; i < dataTypes.size(); i++)
                        profile.setDataElement(dataTypes.get(i), jsonElements.get(i));
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}
