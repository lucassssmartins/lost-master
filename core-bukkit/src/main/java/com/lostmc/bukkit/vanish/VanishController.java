package com.lostmc.bukkit.vanish;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.vanish.PlayerUnvanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.rank.Rank;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

import static com.lostmc.core.translate.Translator.tl;

public class VanishController extends Controller implements Listener {

    private Set<UUID> admins;

    public VanishController(Control control) {
        super(control);
        this.admins = new HashSet<>();
    }

    @Override
    public void onEnable() {
        registerListener(this);
    }

    public void toggleVanish(Player player, PlayerVanishModeEvent.Mode mode) {
        if (mode == null)
            return;
        if (mode == PlayerVanishModeEvent.Mode.VANISH) {
            PlayerVanishModeEvent event = new PlayerVanishModeEvent(player, mode);
            getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;
            if (!admins.contains(player.getUniqueId()))
                admins.add(player.getUniqueId());
            player.setGameMode(GameMode.CREATIVE);
            Rank vanishedFor = updateVanishToOnline(player);
            Locale locale = Profile.getProfile(player).getLocale();
            player.sendMessage(tl(locale, "enter-vanished-mode"));
            player.sendMessage(tl(locale, "vanished-for-rank", vanishedFor.toString()));
            MessageAPI.sendAlert(player.getName() + " entrou no modo vanish");
        } else {
            if (!admins.contains(player.getUniqueId()))
                return;
            PlayerVanishModeEvent event = new PlayerVanishModeEvent(player, mode);
            getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;
            admins.remove(player.getUniqueId());
            player.setGameMode(GameMode.SURVIVAL);
            updateVanishToPlayer(player);
            updateVanishToOnline(player);
            Locale locale = Profile.getProfile(player).getLocale();
            player.sendMessage(tl(locale, "exit-vanished-mode"));
            player.sendMessage(tl(locale, "vanish-visible-for-all"));
            MessageAPI.sendAlert(player.getName() + " saiu do modo vanish");
        }
    }

    public Rank updateVanishToOnline(Player player) {
        Profile profile = Profile.getProfile(player);
        Rank group = profile.getRank();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId()))
                continue;
            Profile onlineP = Profile.getProfile(online);
            if (isVanished(player) && onlineP.getRank().ordinal() > group.ordinal()) {
                PlayerVanishEvent event = new PlayerVanishEvent(player, online);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    if (!online.canSee(player))
                        online.showPlayer(player);
                } else if (online.canSee(player))
                    online.hidePlayer(player);
                continue;
            }
            PlayerUnvanishEvent event = new PlayerUnvanishEvent(player, online);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (online.canSee(player))
                    online.hidePlayer(player);
            } else if (!online.canSee(player))
                online.showPlayer(player);
        }
        return Rank.values()[group.ordinal() + 1];
    }

    public void updateVanishToPlayer(Player player) {
        Rank rank = Profile.getProfile(player).getRank();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId()))
                continue;
            Rank group = Profile.getProfile(online).getRank();
            if (isVanished(online)) {
                if (rank.ordinal() > group.ordinal()) {
                    PlayerVanishEvent event = new PlayerVanishEvent(online, player);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        if (!player.canSee(online))
                            player.showPlayer(online);
                    } else if (player.canSee(online))
                        player.hidePlayer(online);
                    continue;
                }
            }
            PlayerUnvanishEvent event = new PlayerUnvanishEvent(online, player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (player.canSee(online))
                    player.hidePlayer(online);
            } else if (!player.canSee(online)) {
                player.showPlayer(online);
            }
        }
    }

    public boolean isVanished(Player player) {
        return this.admins.contains(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.awardAchievement(Achievement.OPEN_INVENTORY);
        if (player.hasPermission("core.cmd.admin")
                && Profile.getProfile(player).getData(DataType.AUTO_JOIN_VANISHED).getAsBoolean())
            toggleVanish(player, PlayerVanishModeEvent.Mode.VANISH);
        else {
            updateVanishToPlayer(player);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player))
            return;
        Player player = event.getPlayer();
        if (!isVanished(player))
            return;
        event.setCancelled(true);
        player.openInventory(((Player) event.getRightClicked()).getInventory());
        MessageAPI.sendAlert(player.getName() + " abriu o inventário de " + event.getRightClicked().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        admins.remove(id);
    }

    @Override
    public void onDisable() {

    }
}
