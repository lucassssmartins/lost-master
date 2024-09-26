package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketInExpiredPermission;
import com.lostmc.core.networking.PacketOutRemoveRank;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.translate.Translator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ProfileListener extends BukkitListener {

    @EventHandler
    public void onTimerRanks(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getProfile(player);
            List<RankProduct> ranks = profile.getRanks();

            if (ranks.isEmpty())
                continue;

            int ID = -1;

            for (int i = 0; i < ranks.size(); i++) {
                RankProduct rank = ranks.get(i);
                if (rank.isLifetime())
                    continue;
                if (!rank.isExpired())
                    continue;
                ID = i;
                break;
            }

            if (ID == -1)
                continue;

            RankProduct rank = profile.removeRank(ID);

            if (rank == null)
                continue;

            profile.save();
            BukkitPlugin.getInstance().getPermissionManager().updatePermissions(player);
            if (!player.hasPermission("tag." + profile.getTag().toString().toLowerCase()))
                BukkitPlugin.getControl().getController(NametagController.class).setNametag(player,
                        Tag.fromRank(rank.getObject()));
            player.sendMessage(Translator.tl(profile.getLocale(), "core.translation.rank-expired",
                    Tag.fromRank(rank.getObject()).getFormattedName()));

            Commons.getRedisBackend().publish(PacketType.EXPIRED_RANK.toString(),
                    new PacketOutRemoveRank(player.getUniqueId(), ID).toJson());
        }
    }

    @EventHandler
    public void onTimerPermissions(ServerTimerEvent event) {
        if (event.getCurrentTick() % 40 != 0)
            return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getProfile(player);
            List<PermissionProduct> ranks = profile.getPermissions();

            if (ranks.isEmpty())
                continue;

            int ID = -1;

            for (int i = 0; i < ranks.size(); i++) {
                PermissionProduct rank = ranks.get(i);
                if (rank.isLifetime())
                    continue;
                if (!rank.isExpired())
                    continue;
                ID = i;
                break;
            }

            if (ID == -1)
                continue;

            PermissionProduct rank;

            try {
                rank = profile.getPermissions().remove(ID);
            } catch (Exception e) {
                continue;
            }

            if (rank == null)
                continue;

            profile.save();
            BukkitPlugin.getInstance().getPermissionManager().updatePermissions(player);
            if (!player.hasPermission("tag." + profile.getTag().toString().toLowerCase()))
                BukkitPlugin.getControl().getController(NametagController.class).setNametag(player,
                        Tag.fromRank(profile.getRank()));
            player.sendMessage("§cA permissão '" + rank.getObject() + "' em sua conta expirou.");

                Commons.getRedisBackend().publish(PacketType.EXPIRED_IN_PERMISSION.toString(),
                    new PacketInExpiredPermission(player.getUniqueId(), ID).toJson());
        }
    }
}
