package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PreLoginStartListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void login(AsyncPlayerPreLoginEvent event) {
        UUID uniqueId = event.getUniqueId();

        if (!Commons.isSystemReady()) {
            event.setKickMessage("§cSystem initializing...");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if (Bukkit.getPlayer(uniqueId) != null) {
            event.setKickMessage("§cERR_ALREADY_ONLINE");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        Profile profile = null;

        try {
            profile = Commons.getRedisBackend().getRedisProfile(uniqueId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (profile == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("§cERR_PROFILE_NULL");
            }
        }

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        Commons.getProfileMap().put(profile.getUniqueId(), profile);
        BukkitPlugin.getInstance().onProfileLoad(profile);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quit(PlayerQuitEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskLater(BukkitPlugin.getInstance(),
                () -> BukkitPlugin.getInstance().onProfileUnload(Commons.getProfileMap().
                remove(uniqueId)), 2L);
    }
}
