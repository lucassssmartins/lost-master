package com.lostmc.pvp.warp;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.api.scoreboard.ScoreboardModel;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.game.event.combatlog.PlayerDeathOutOfCombatEvent;
import com.lostmc.game.interfaces.Ejectable;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.event.PlayerWarpJoinEvent;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import com.lostmc.pvp.warp.registry.FightWarp;
import com.lostmc.pvp.warp.registry.LavaWarp;
import com.lostmc.pvp.warp.registry.SpawnWarp;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public abstract class Warp implements Listener {

    private WarpController controller;
    @Setter
    private String name;
    private Map<UUID, Boolean> players = new HashMap<>();
    @Setter
    private Location location;
    @Setter
    private boolean fallKitWarp = false;
    @Setter
    private Class<? extends WarpScoreboardModel> scoreboardModelClass;

    public Warp(WarpController controller) {
        this.controller = controller;
    }

    public void joinPlayer(Player player, Warp joiningFrom) {
        if (!player.isOnline())
            return;
        if (joiningFrom != null) {
            joiningFrom.leavePlayer(player);
        }

        player.closeInventory();

        try {
            ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        } catch (Exception e) {
        }

        this.players.put(player.getUniqueId(), true);

        Profile profile = Profile.getProfile(player);
        PvPGamer gamer = (PvPGamer) profile.getResource(Gamer.class);

        for (Kit ability : gamer.getKits().values()) {
            ability.removeCooldown(player);
            if (!(ability instanceof Ejectable))
                continue;
            ((Ejectable) ability).eject(player);
        }

        gamer.setWarp(this);
        gamer.getCombatLog().logout();

        getController().getServer().getPluginManager().callEvent(new PlayerWarpJoinEvent(player, this));

        if (this instanceof ArenaWarp) {
            List<ILocation> spawns = ((ArenaWarp) this).getRandomTp();
            if (!spawns.isEmpty()) {
                ILocation tp = spawns.get(Commons.getRandom().nextInt(spawns.size() - 1));
                player.teleport(tp.toLocation(player.getWorld()));
            } else if (this.location != null) {
                player.teleport(this.location);
            }
        } else if (this.location != null) {
            player.teleport(this.location);
        }

        if (this.scoreboardModelClass != null) {
            try {
                Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard(player);
                ScoreboardModel scoreboardModel = this.scoreboardModelClass.getConstructor(Scoreboard.class)
                        .newInstance(scoreboard);
                scoreboard.setModel(scoreboardModel);
            } catch (Exception e) {

            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setHealth(player.getMaxHealth());
        player.setExp(0);
        player.setFireTicks(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        this.onPlayerJoin(player);
        player.updateInventory();
    }

    public void leavePlayer(Player player) {
        this.players.remove(player.getUniqueId());
        this.onPlayerLeave(player);
    }

    public Set<Player> getPlayers() {
        Set<Player> playerSet = new HashSet<>();
        for (UUID uuid : this.players.keySet()) {
            Player next = Bukkit.getPlayer(uuid);
            if (next == null)
                continue;
            playerSet.add(next);
        }
        return playerSet;
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public boolean containsPlayer(Player player) {
        return this.players.containsKey(player.getUniqueId());
    }

    public boolean isProtected(Player player) {
        return containsPlayer(player) && Boolean.valueOf(this.players.get(player.getUniqueId()));
    }

    public void setProtected(Player player, boolean protect) {
        this.players.put(player.getUniqueId(), protect);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitListener(PlayerQuitEvent e) {
        if (containsPlayer(e.getPlayer())) {
            this.leavePlayer(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageListener(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PvPGamer gamer = (PvPGamer) Profile.getProfile(player).getResource(Gamer.class);
            Warp warp = gamer.getWarp();
            if (warp.equals(this)) {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    event.setCancelled(true);
                    if (getController().getWarpByClass(SpawnWarp.class).containsPlayer(player) ||
                            getController().getWarpByClass(LavaWarp.class).containsPlayer(player) ||
                            getController().getWarpByClass(FightWarp.class).containsPlayer(player)) {
                        if (warp.getLocation() != null) {
                            player.setFallDistance(0);
                            player.teleport(warp.getLocation());
                        }
                    } else {
                        Player combatPlayer = null;
                        CombatLog combatLog = gamer.getCombatLog();
                        if (combatLog.isLogged())
                            combatPlayer = Bukkit.getPlayer(combatLog.getCombatLogged());
                        if (combatPlayer != null) {
                            Bukkit.getPluginManager().callEvent(new PlayerDeathInCombatEvent(player, combatPlayer, false));
                        } else {
                            Bukkit.getPluginManager().callEvent(new PlayerDeathOutOfCombatEvent(player));
                        }
                    }
                    return;
                }
            }
            if (!containsPlayer(player))
                return;
            if (!isProtected(player))
                return;
            event.setCancelled(true);
            if (this.fallKitWarp && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) &&
                    !PvP.getControl().getController(VanishController.class).isVanished(player)) {
                setProtected(player, false);
                this.onProtectionLost(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player entity = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        PvPGamer entityGamer = (PvPGamer) Profile.getProfile(entity).getResource(Gamer.class);
        PvPGamer damagerGamer = (PvPGamer) Profile.getProfile(damager).getResource(Gamer.class);
        if (!entityGamer.getWarp().equals(damagerGamer.getWarp())) {
            event.setCancelled(true);
            return;
        }

        if (isProtected(damager) && !isProtected(entity)) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Warp) {
            Warp that = (Warp) o;
            return that.name.equals(this.name);
        }
        return false;
    }

    public abstract void onPlayerJoin(Player player);

    public abstract void onProtectionLost(Player player);

    public abstract void onPlayerLeave(Player player);
}
