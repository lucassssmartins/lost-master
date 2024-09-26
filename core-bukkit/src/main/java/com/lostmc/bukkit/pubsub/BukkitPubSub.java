package com.lostmc.bukkit.pubsub;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.*;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.report.Report;
import com.lostmc.core.translate.Translator;
import com.lostmc.core.utils.DateUtils;
import com.lostmc.core.utils.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class BukkitPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        PacketType type = null;
        try {
            type = PacketType.valueOf(channel);
        } catch (Exception e) {
            BukkitPlugin.getInstance().getLogger().info("Unknown package type: " + channel);
            return;
        }

        switch (type) {
            case ADD_RANK: {
                PacketOutAddRank pakage = JsonUtils.jsonToObject(message, PacketOutAddRank.class);
                Player player = Bukkit.getPlayer(pakage.getPlayerUniqueId());
                if (player != null) {
                    Profile profile = Profile.getProfile(player);
                    RankProduct product = pakage.getRankProduct();
                    profile.addRank(pakage.getRankProduct());
                    BukkitPlugin.getInstance().getPermissionManager().updatePermissions(player);
                    BukkitPlugin.getControl().getController(NametagController.class).setNametag(player,
                            Tag.fromRank(product.getObject()));
                    TitleAPI.setTitle(player, Translator.tl(profile.getLocale(), "core.translations.new-rank-received"),
                            Tag.fromRank(product.getObject()).getColouredName(true));
                    if (product.isLifetime())
                        player.sendMessage(
                                Translator.tl(profile.getLocale(), "core.translations.received-eternal-rank",
                                        Tag.fromRank(product.getObject()).getColouredName(false)));
                    else
                        player.sendMessage(Translator.tl(profile.getLocale(), "core.translations.received-temp-rank",
                                Tag.fromRank(product.getObject()).getColouredName(false),
                                DateUtils.getTime(product.getExpirationTime())));
                }
                break;
            }
            case REMOVE_RANK: {
                PacketOutRemoveRank packet = JsonUtils.jsonToObject(message, PacketOutRemoveRank.class);
                Player player = Bukkit.getPlayer(packet.getPlayerUniqueId());
                if (player != null) {
                    Profile profile = Profile.getProfile(player);
                    RankProduct rank = profile.removeRank(packet.getIndex());
                    if (rank != null) {
                        BukkitPlugin.getInstance().getPermissionManager().updatePermissions(player);
                        if (!player.hasPermission("tag." + rank.getObject().toString().toLowerCase()))
                            BukkitPlugin.getControl().getController(NametagController.class).setNametag(player,
                                    Tag.fromRank(profile.getRank()));
                        player.sendMessage(Translator.tl(profile.getLocale(), "core.translations.removed-rank",
                                Tag.fromRank(rank.getObject()).getFormattedName()));
                    }
                }
                break;
            }
            case EXPIRED_RANK: {
                break;
            }
            case UPDATE_IN_SINGLE_DATA: {
                break;
            }
            case UPDATE_OUT_SINGLE_DATA: {
                PacketUpdateSingleData pakage = JsonUtils.jsonToObject(message, PacketUpdateSingleData.class);
                Profile profile = Profile.fromUniqueId(pakage.getUniqueId());
                if (profile != null) {
                    profile.setDataElement(pakage.getDataType(), pakage.getJsonElement());
                }
                break;
            }
            case RELOAD_TRANSLATIONS: {
                Commons.getPlatform().runAsync(() -> {
                    Translator.reloadTranslations();
                });
                break;
            }
            case REPORT_CREATE: {
                PacketReport packet = JsonUtils.jsonToObject(message, PacketReport.class);
                Report report = packet.getReport();
                Commons.getReportMap().put(report.getPlayerUniqueId(), report);
                break;
            }
            case REPORT_REMOVE: {
                PacketReport packet = JsonUtils.jsonToObject(message, PacketReport.class);
                Report report = packet.getReport();
                Commons.getReportMap().remove(report.getPlayerUniqueId());
                break;
            }
            case ADD_OUT_PERMISSION: {
                PacketOutAddPermission packet = JsonUtils.jsonToObject(message, PacketOutAddPermission.class);
                Player p = Bukkit.getPlayer(packet.getUniqueId());
                if (p != null) {
                    Profile profile = Commons.getProfileMap().get(p.getUniqueId());
                    if (profile != null) {
                        profile.addPermission(packet.getPermission());
                        BukkitPlugin.getInstance().getPermissionManager().updatePermissions(p);
                    }
                }
            }
            case REMOVE_OUT_PERMISSIONS: {
                PacketOutRemovePermission packet = JsonUtils.jsonToObject(message, PacketOutRemovePermission.class);
                Player p = Bukkit.getPlayer(packet.getUniqueId());
                if (p != null) {
                    Profile profile = Commons.getProfileMap().get(p.getUniqueId());
                    if (profile != null) {
                        profile.removePermissions(packet.getPermission());
                        BukkitPlugin.getInstance().getPermissionManager().updatePermissions(p);
                        if (!p.hasPermission("tag." + profile.getTag().toString().toLowerCase())) {
                            BukkitPlugin.getControl().getController(NametagController.class)
                                    .setNametag(p, Tag.fromRank(profile.getRank()));
                        }
                    }
                }
            }
            default: {
                break;
            }
        }
    }
}
