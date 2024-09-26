package com.lostmc.hungergames.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.game.event.player.PlayerPreDeathEvent;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.manager.AssistManager;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.status.StatusHandler;
import com.lostmc.hungergames.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathListener extends HungerListener {

    private HungerGamesMode hg;
    public static Set<UUID> relogProcess = new HashSet<>();
    private static Set<UUID> playerRelogged = new HashSet<>();

    public DeathListener() {
        hg = (HungerGamesMode) getMain().getGameMode();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (!getMain().getGameStage().isPregame()) {
            if (relogProcess.contains(p.getUniqueId()))
                event.allow();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (relogProcess.contains(p.getUniqueId())) {
            playerRelogged.add(p.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    relogProcess.remove(p.getUniqueId());
                }
            }.runTaskLater(getMain(), 60 * 20);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        p.setHealth(p.getMaxHealth());
        event.setDeathMessage(null);
        event.getDrops().clear();
        p.setFoodLevel(20);
        p.setSaturation(5);
        p.setFireTicks(0);
        p.setExp(0);
        HungerGamer gamer = HungerGamer.getGamer(p);
        if (gamer.isNotPlaying())
            return;
        if (!getMain().getGladiatorFightController().isInFight(p))
            dropItems(p);
        if (p.hasPermission("hg.respawn")) {
            if (getMain().getTimer() <= 300) {
                Random r = new Random();
                int x = 100 + r.nextInt(400);
                int z = 100 + r.nextInt(400);
                if (r.nextBoolean())
                    x = -x;
                if (r.nextBoolean())
                    z = -z;
                World world = Bukkit.getWorld("world");
                int y = world.getHighestBlockYAt(x, z);
                Location loc = new Location(world, x, y + 1, z);
                loc.getChunk().load(true);
                p.teleport(loc);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Kit kit : gamer.getKits().values()) {
                            if (kit.getItems() != null) {
                                p.getInventory().addItem(kit.getItems());
                            }
                        }
                        p.getInventory().addItem(new ItemStack(Material.COMPASS));
                    }
                }.runTaskLater(getMain(), 1);
                return;
            }
        }
        DamageCause cause = p.getLastDamageCause() == null ? DamageCause.CUSTOM
                : p.getLastDamageCause().getCause();
        Player killer = p.getKiller();
        if (killer == null && gamer.getCombatLog().isLogged())
            killer = Bukkit.getPlayer(gamer.getCombatLog().getCombatLogged());
        StatusHandler.addDeath(p);
        if (killer != null) {
            Bukkit.broadcastMessage(ChatColor.AQUA + killer.getName() + HungerGamer.getGamer(killer).getKits().values() +
                    " matou " + p.getName() + gamer.getKits().values() + " usando sua " + getItem(killer.getItemInHand().getType()));
            StatusHandler.addKill(killer, p);
        } else {
            Bukkit.broadcastMessage(ChatColor.AQUA + p.getName() + gamer.getKits().values() + " morreu " + getCause(cause));
        }
        if (p.hasPermission("core.cmd.admin")) {
            VanishController manager = HungerGames.getControl().getController(VanishController.class);
            gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
            if (!manager.isVanished(p))
                manager.toggleVanish(p, PlayerVanishModeEvent.Mode.VANISH);
        } else if (p.hasPermission("hg.spectate")) {
            gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
        } else {
            gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("REPLAY_HGMIX");
            p.sendPluginMessage(getMain(), "BungeeCord", output.toByteArray());
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (p != null && p.isOnline()) {
                        p.kickPlayer("§cno_permission");
                    }
                }
            }.runTaskLater(getMain(), 10L);
        }
        hg.checkWinner();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        event.setQuitMessage(null);
        if (p.isDead())
            return;
        final HungerGamer gamer = HungerGamer.getGamer(p);
        if (gamer.isNotPlaying())
            return;
        if (!relogProcess.contains(p.getUniqueId())) {
            Bukkit.broadcastMessage("§7" + p.getName() + " saiu do servidor");
            relogProcess.add(p.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerRelogged.remove(p.getUniqueId());
                    if (!p.isOnline()) {
                        Bukkit.broadcastMessage("§b" + p.getName() + gamer.getKits().values() + " demorou para relogar" +
                                " e foi eliminado");
                        dropItems(p);
                        gamer.remove();
                        relogProcess.remove(p.getUniqueId());
                        hg.checkWinner();
                    }
                }
            }.runTaskLater(getMain(), 60 * 20);
            return;
        }
        if (getMain().getGameStage() == GameStage.INVINCIBILITY || getMain().getGameStage() == GameStage.GAMETIME) {
            Bukkit.broadcastMessage("§b" + p.getName() + gamer.getKits().values() + " deslogou " +
                    "e foi eliminado");
            dropItems(p);
            StatusHandler.addDeath(p);
            Management.getManagement(AssistManager.class).checkForAssistences(p);
            gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
            hg.checkWinner();
        }
    }

    private String getItem(Material type) {
        String cause;
        if (type.equals(Material.BOWL)) {
            cause = "tigela";
        } else if (type.equals(Material.MUSHROOM_SOUP)) {
            cause = "sopa";
        } else if (type.equals(Material.COMPASS)) {
            cause = "bússola";
        } else if (type.equals(Material.STICK)) {
            cause = "madeira";
        } else if (type.equals(Material.IRON_INGOT)) {
            cause = "barra de ferro";
        } else if (type.equals(Material.GOLD_INGOT)) {
            cause = "barra de ouro";
        } else if (type.equals(Material.BOW)) {
            cause = "arco";
        } else if (type.equals(Material.WOOD_SWORD)) {
            cause = "espada de madeira";
        } else if (type.equals(Material.STONE_SWORD)) {
            cause = "espada de pedra";
        } else if (type.equals(Material.IRON_SWORD)) {
            cause = "espada de ferro";
        } else if (type.equals(Material.DIAMOND_SWORD)) {
            cause = "espada de diamante";
        } else if (type.equals(Material.WOOD_AXE)) {
            cause = "machado de madeira";
        } else if (type.equals(Material.STONE_AXE)) {
            cause = "machado de pedra";
        } else if (type.equals(Material.IRON_AXE)) {
            cause = "machado de ferro";
        } else if (type.equals(Material.DIAMOND_AXE)) {
            cause = "machado de diamante";
        } else {
            cause = "mão";
        }
        return cause;
    }

    private String getCause(EntityDamageEvent.DamageCause deathCause) {
        String cause;
        if (deathCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            cause = "atacado por um monstro";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.CUSTOM)) {
            cause = "de uma forma não conhecida";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            cause = "explodido em mil pedaços";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            cause = "explodido por um monstro";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.CONTACT)) {
            cause = "abraçando um cacto";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.FALL)) {
            cause = "esquecendo de abrir os paraquedas";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)) {
            cause = "stompado por um bloco";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            cause = "pegando fogo";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.LAVA)) {
            cause = "nadando na lava";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
            cause = "atingido por um raio";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.MAGIC)) {
            cause = "atingido por uma magia";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.MELTING)) {
            cause = "atingido por um boneco de neve";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.POISON)) {
            cause = "envenenado";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            cause = "atingido por um projetil";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.STARVATION)) {
            cause = "de fome";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            cause = "sufocado";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.SUICIDE)) {
            cause = "se suicidando";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.THORNS)) {
            cause = "encostando em alguns espinhos";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.VOID)) {
            cause = "pela pressão do void";
        } else if (deathCause.equals(EntityDamageEvent.DamageCause.WITHER)) {
            cause = "pelo efeito do whiter";
        } else {
            cause = "e foi eliminado";
        }
        return cause;
    }

    public static void dropItems(Player p) {
        ArrayList<ItemStack> items = new ArrayList<>();
        PlayerInventory inv = p.getInventory();
        for (ItemStack item : inv.getContents())
            if (checkNotNull(item))
                items.add(item.clone());
        for (ItemStack item : inv.getArmorContents())
            if (checkNotNull(item))
                items.add(item.clone());
        if (checkNotNull(p.getItemOnCursor()))
            items.add(p.getItemOnCursor().clone());
        ItemUtils.dropAndClear(p, items, p.getLocation());
    }

    private static boolean checkNotNull(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
}
