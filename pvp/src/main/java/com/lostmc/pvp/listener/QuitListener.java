package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.interfaces.Ejectable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitListener(PlayerQuitEvent event) {
        event.setQuitMessage("");

        Player player = event.getPlayer();
        Gamer gamer = Profile.getProfile(event.getPlayer()).getResource(Gamer.class);

        for (Kit ability : gamer.getKits().values()) {
            if (ability instanceof Ejectable) {
                ((Ejectable) ability).eject(player);
            }
        }
    }
}
