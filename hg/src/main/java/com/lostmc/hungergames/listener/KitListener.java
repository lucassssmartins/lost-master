package com.lostmc.hungergames.listener;

import com.lostmc.game.interfaces.Ejectable;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class KitListener extends HungerListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        HungerGamer gamer = HungerGamer.getGamer(p);
        if (gamer.hasPrimaryKit() && gamer.getPrimaryKit() instanceof Ejectable)
            ((Ejectable) gamer.getPrimaryKit()).eject(p);
        if (gamer.hasSecondaryKit() && gamer.getSecondaryKit() instanceof Ejectable) {
            ((Ejectable) gamer.getSecondaryKit()).eject(p);
        }
    }
}
