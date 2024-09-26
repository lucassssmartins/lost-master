package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener extends BukkitListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (BukkitPlugin.getControl().getController(VanishController.class)
                .isVanished(p))
            return;
        Profile profile = Profile.getProfile(p);
        if (profile == null)
            return;
        Rank ranking = profile.getRank();
        if (ranking == Rank.DEFAULT)
            return;
        boolean isNicked = !profile.getData(DataType.NICKNAME).getAsString().isEmpty();
        if (Commons.getProxyHandler().getLocal().getServerType().isHub()) {
            if (isNicked) {
                p.sendMessage("§cYou are currently nicked!");
            } else {
                Bukkit.broadcastMessage(Tag.fromRank(ranking).getPrefix() + p.getName() + " §6entrou no lobby!");
            }
        }
    }
}
