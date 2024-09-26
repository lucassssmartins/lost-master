package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.event.game.GameStartEvent;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.stage.GameStage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class GameListener extends HungerListener {

    private Random random = new Random();
    private Set<UUID> joined = new HashSet<>();
    public static boolean spectatorEnabled = true;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (getMain().getGameStage().isPregame())
            return;
        if (p.hasPermission("core.cmd.admin"))
            return;
        if (p.hasPermission("hg.spectate") && spectatorEnabled)
            return;
        if (p.hasPermission("hg.respawn") && !joined.contains(p.getUniqueId())
                && getMain().getTimer() < 300) {
            return;
        }
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO torneio já iniciou!\nAdquira VIP para poder espectar" +
                "\nwww.lostmc.com.br");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player p = event.getPlayer();
        HungerGamer gamer = HungerGamer.getGamer(p);
        if (DeathListener.relogProcess.contains(p.getUniqueId()))
            return;
        if ((getMain().getGameStage().isInvincibility()
                || (getMain().getGameStage().isGametime() && getMain().getTimer() <= 300))
                && !joined.contains(p.getUniqueId())) {
            if (!HungerGames.getControl().getController(VanishController.class)
                    .isVanished(p)) {
                gamer.setGamerState(HungerGamer.GamerState.ALIVE);
                Bukkit.broadcastMessage("§7" + p.getName() + " entrou no servidor");
                p.getInventory().clear();
                for (PotionEffect effect : p.getActivePotionEffects())
                    p.removePotionEffect(effect.getType());
                for (Kit kit : gamer.getKits().values()) {
                    if (kit.getItems() != null) {
                        p.getInventory().addItem(kit.getItems());
                    }
                }
                p.setGameMode(GameMode.SURVIVAL);
                p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                p.updateInventory();
                joined.add(p.getUniqueId());
            }
        } else {
            if (!p.hasPermission("core.cmd.admin")) {
                gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
            } else if (!Profile.getProfile(p).getData(DataType.AUTO_JOIN_VANISHED)
                    .getAsBoolean()) {
                gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null)
            return;
        for (Kit kit : HungerGamer.getGamer(p).getKits().values()) {
            if (kit.isMainItem(item)) {
                event.setCancelled(true);
                p.updateInventory();
                break;
            }
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL ||
                event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e) {
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCompass(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        HungerGamer hg = HungerGamer.getGamer(p);
        if (getMain().getGameStage() == GameStage.PREGAME)
            return;
        if (hg.isNotPlaying() ||
                HungerGames.getControl().getController(VanishController.class).isVanished(p))
            return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR)
            return;
        if (item.getType() == Material.COMPASS) {
            Player target = null;
            double distance = 10000;
            for (HungerGamer gamer : Management.getManagement(GamerManager.class).getAliveGamers()) {
                Player game = Bukkit.getPlayer(gamer.getUniqueId());
                if (game == null || !game.isOnline())
                    continue;
                double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
                if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 25) {
                    distance = distOfPlayerToVictim;
                    target = game;
                }
            }
            if (target == null) {
                p.sendMessage("§cNenhum jogador encontrado, apontando para o spawn.");
                p.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
            } else {
                p.setCompassTarget(target.getLocation());
                p.sendMessage("§eBússola apontando para §b" + target.getName());
            }
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        getMain().getServer().getScheduler().runTaskLater(getMain(), () -> {
            if (!item.isDead() && item.isOnGround()) {
                item.remove();
            }
        }, (60 * 20));
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            ((Player) event.getEntity()).setSaturation(5f);
        }
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.PIG_ZOMBIE) {
            event.setCancelled(true);
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;
        if (random.nextInt(5) != 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            joined.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (getMain().getGameStage() == GameStage.ENDING)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        VanishController manager = HungerGames.getControl().getController(VanishController.class);
        if (manager.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
