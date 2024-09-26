package com.lostmc.lobby.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.controller.LobbyController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class MoveListener extends BukkitListener {

    @EventHandler
    public void onLaunch(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (event.getTo().getY() <= 0) {
            event.setTo(Lobby.getControl().getController(LobbyController.class)
                    .getSpawnLocation(event.getPlayer().getWorld()));
            return;
        }
        if (!p.isOnGround())
            return;
        Location standBlock = p.getLocation().clone().add(0, -1, 0);
        if (standBlock.getBlock().getType() == Material.EMERALD_BLOCK) {
            Vector vector = p.getLocation().getDirection();
            if (Commons.getProxyHandler().getLocal().getServerType() == ServerType.HG_LOBBY)
                vector.multiply(3.91F).setY(0.5F);
            else
                vector.multiply(1.51F).setY(0.5F);
            p.setVelocity(vector);
            p.playSound(p.getLocation(), Sound.HORSE_JUMP, 10.0f, 1.0f);
        }
    }
}
