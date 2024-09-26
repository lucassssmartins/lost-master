package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.api.nick.NickAPI;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Profile profile = Profile.getProfile(event.getPlayer());
        String nickname = profile.getData(DataType.NICKNAME).getAsString();
        if (!nickname.isEmpty())
            NickAPI.changePlayerName(event.getPlayer(), nickname, false);
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if (event.getPlayer().hasPermission("core.advantages.joinfull")) {
                event.allow();
            }
        }
    }
}
