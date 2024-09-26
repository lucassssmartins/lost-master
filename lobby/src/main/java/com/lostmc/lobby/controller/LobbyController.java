package com.lostmc.lobby.controller;

import com.lostmc.bukkit.api.cooldown.CooldownAPI;
import com.lostmc.bukkit.api.cooldown.event.CooldownDisplayEvent;
import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.menu.MyProfileMenu;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.translate.Translator;
import com.lostmc.lobby.menu.collectables.CollectablesGenMenu;
import com.lostmc.lobby.menu.lobbies.LobbiesMenu;
import com.lostmc.lobby.menu.games.GameSelectorMenu;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class LobbyController extends Controller implements Listener {

    public LobbyController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        registerListener(this);
    }

    private final String COOLDOWN_CHANGE_VISIBILITY_NAME = "gen-hub.players-visibility";

    @EventHandler
    public void display(CooldownDisplayEvent event) {
        if (event.getCooldown().getName().equals(COOLDOWN_CHANGE_VISIBILITY_NAME)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private void changePlayersVisibility(Player player, ItemStack item) {
        if (!CooldownAPI.hasCooldown(player, COOLDOWN_CHANGE_VISIBILITY_NAME)) {
            InteractHandler handler = new InteractHandler() {


                @Override
                public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                    changePlayersVisibility(player, item);
                    return true;
                }
            };

            ItemBuilder builder = new ItemBuilder(Material.INK_SACK);
            ItemStack stack;

            Profile profile = Profile.getProfile(player);

            if (profile.getData(DataType.HUB_PLAYERS).getAsBoolean()) {
                stack = builder.setDurability(8)
                        .setName(Translator.tl(profile.getLocale(), "lobby-players-visiblity-item-name") +
                                " " + ChatColor.RED + "OFF").build(handler);
                CooldownAPI.addCooldown(player, new Cooldown(COOLDOWN_CHANGE_VISIBILITY_NAME, 3L));
                profile.setData(DataType.HUB_PLAYERS, false);
                profile.save();
                player.setItemInHand(stack);
                // TODO: hide players
                player.sendMessage(Translator.tl(profile.getLocale(), "lobby-players-visibility.now-insivible"));
            } else {
                stack = builder.setDurability(10)
                        .setName(Translator.tl(profile.getLocale(), "lobby-players-visiblity-item-name") +
                                " " + ChatColor.GREEN + "ON").build(handler);
                CooldownAPI.addCooldown(player, new Cooldown(COOLDOWN_CHANGE_VISIBILITY_NAME, 3L));
                profile.setData(DataType.HUB_PLAYERS, true);
                profile.save();
                player.setItemInHand(stack);
                // TODO: show players
                player.sendMessage(Translator.tl(profile.getLocale(), "lobby-players-visibility.now-visible"));
            }

            player.updateInventory();
        } else {
            player.sendMessage(Translator.tl(
                    Profile.getProfile(player).getLocale(), "wait-to-use-again",
                    CooldownAPI.getCooldownFormat(player, COOLDOWN_CHANGE_VISIBILITY_NAME)));
        }
    }

    public void updateBossBar(Player p) {

    }

    public void updateTabList(Player p) {
        Profile profile = Profile.getProfile(p);
        p.setPlayerListHeaderFooter(new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.header")),
                new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.footer")));
    }

    public void updateHotbar(Player p) {
        p.getInventory().clear();
        Locale locale = Profile.getProfile(p).getLocale();
        Inventory inventory = p.getInventory();

        inventory.setItem(0, new ItemBuilder(Material.COMPASS).setName(
                Translator.tl(locale, "hub.hotbar-itens.games")).build(new InteractHandler() {
            @Override
            public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                player.openInventory(new GameSelectorMenu(player, Profile.getProfile(player)));
                return true;
            }
        }));

        GameProfile gameProfile = ((CraftPlayer) p).getHandle().getProfile();
        inventory.setItem(1, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setName(
                Translator.tl(locale, "hub.hotbar-itens.profile")).setPlayerHead(gameProfile).build(new InteractHandler() {
            @Override
            public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                player.performCommand("profile");
                return true;
            }
        }));

        inventory.setItem(4, new ItemBuilder(Material.getMaterial(349)).setDurability(3).setName(
                Translator.tl(locale, "hub.hotbar-itens.cosmetics")).build(new InteractHandler() {
            @Override
            public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                player.openInventory(new CollectablesGenMenu(Profile.getProfile(player)));
                return true;
            }
        }));

        Profile profile = Profile.getProfile(p);
        boolean players = profile.getData(DataType.HUB_PLAYERS).getAsBoolean();

        inventory.setItem(7, new ItemBuilder(Material.INK_SACK).setDurability(
                        players ? 10 : 8).setName(
                        Translator.tl(profile.getLocale(), "lobby-players-visiblity-item-name") +
                                " " + (players ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF")).
                build(new InteractHandler() {
                    @Override
                    public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                        changePlayersVisibility(player, item);
                        return true;
                    }
                }));

        inventory.setItem(8, new ItemBuilder(Material.NETHER_STAR).setName(
                Translator.tl(locale, "hub.hotbar-itens.lobbies")).build(new InteractHandler() {
            @Override
            public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                player.openInventory(new LobbiesMenu(player, Profile.getProfile(player).getLocale()));
                return true;
            }
        }));
        p.updateInventory();
    }

    public Location getSpawnLocation(World world) {
        if (getPlugin().getConfig().isSet("spawn-location")) {
            return Commons.getGson().fromJson(getPlugin().getConfig().getString("spawn-location"),
                    ILocation.class).toLocation(world);
        }
        return world.getSpawnLocation();
    }

    public void saveSpawnLocation(Location spawn) {
        getPlugin().getConfig().set("spawn-location", Commons.getGson().toJson(new ILocation(spawn)));
        getPlugin().saveConfig();
    }

    @Override
    public void onDisable() {

    }
}
