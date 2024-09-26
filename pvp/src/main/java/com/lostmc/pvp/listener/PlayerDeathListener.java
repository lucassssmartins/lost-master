package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.game.event.combatlog.PlayerDeathOutOfCombatEvent;
import com.lostmc.game.event.player.PlayerPreDeathEvent;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.status.StatusHandler;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.event.PlayerDeathInWarpEvent;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import com.lostmc.pvp.warp.registry.FightWarp;
import com.lostmc.pvp.warp.registry.SpawnWarp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreDeath(PlayerPreDeathEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathOutOfCombat(PlayerDeathOutOfCombatEvent e) {
        Player player = e.getPlayer();
        Profile profile = Profile.getProfile(player);
        PvPGamer gamer = (PvPGamer) profile.getResource(Gamer.class);
        Warp warp = gamer.getWarp();
        boolean deadMessage = true;
        if (warp instanceof FightWarp) {
            FightWarp.Fight fight = ((FightWarp) warp).getFight(player);
            if (fight != null) {
                deadMessage = false;
                StatusHandler.updateStatus(player, fight.getOponent(player));
            }
        }
        if (deadMessage)
            player.sendMessage("§cVocê morreu.");
        PlayerDeathInWarpEvent event = new PlayerDeathInWarpEvent(player, ((PvPGamer) Profile.getProfile(e.getPlayer())
                .getResource(Gamer.class)).getWarp(), true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isDropItems())
            ((PvP) PvP.getInstance()).getItemManager().dropItems(player, player.getLocation());
        handleRespawn(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathInCombat(PlayerDeathInCombatEvent e) {
        Player player = e.getPlayer();
        Profile profile = Profile.getProfile(player);
        PvPGamer gamer = (PvPGamer) profile.getResource(Gamer.class);
        Warp warp = gamer.getWarp();
        if (e.isLoggedOut()) {
            if (!(warp instanceof FightWarp)) {
                StatusHandler.updateStatus(e.getCombatPlayer(), player);
                PlayerDeathInWarpEvent event = new PlayerDeathInWarpEvent(player, ((PvPGamer) Profile.getProfile(e.getPlayer())
                        .getResource(Gamer.class)).getWarp(), true);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isDropItems())
                    ((PvP) PvP.getInstance()).getItemManager().dropItems(player, player.getLocation());
            }
        } else {
            StatusHandler.updateStatus(e.getCombatPlayer(), player);
            PlayerDeathInWarpEvent event = new PlayerDeathInWarpEvent(player, ((PvPGamer) Profile.getProfile(e.getPlayer())
                    .getResource(Gamer.class)).getWarp(), true);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isDropItems())
                ((PvP) PvP.getInstance()).getItemManager().dropItems(player, player.getLocation());
        }

        handleRespawn(player);
    }

    @EventHandler
    public void onPlayerDeathListener(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();
    }

    @EventHandler
    public void onRespawnListener(PlayerRespawnEvent event) {
        event.getPlayer().kickPlayer("§cERR_RESPAWN");
    }

    public void handleRespawn(Player player) {
        Profile profile = Profile.getProfile(player);
        PvPGamer gamer = (PvPGamer) profile.getResource(Gamer.class);
        Warp warp = gamer.getWarp();
        if (warp instanceof ArenaWarp) {
            PvP.getControl().getController(WarpController.class)
                    .getWarpByClass(SpawnWarp.class).joinPlayer(player, warp);
        } else {
            warp.joinPlayer(player, warp);
        }
    }
}