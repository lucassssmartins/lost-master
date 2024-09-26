package com.lostmc.login.manager;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LoginManager extends Controller implements Listener {

    static String MENU_NAME = "§8Captcha";

    private Map<UUID, Integer> waitTick = new HashMap<>();
    private Set<UUID> inCaptcha = new HashSet<>();

    private ItemStack invalidItem = new ItemBuilder(Material.INK_SACK).setName("§cNão clique aqui!")
            .setDurability(1).build(new InteractHandler() {
                @Override
                public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                    player.kickPlayer("§cVocê falhou no captcha! Tente novamente.");
                    return true;
                }
            });
    private ItemStack validItem = new ItemBuilder(Material.INK_SACK).setName("§aClique aqui!")
            .setDurability(10).build(new InteractHandler() {
                @Override
                public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                    inCaptcha.remove(player.getUniqueId());
                    player.closeInventory();
                    return true;
                }
            });

    public LoginManager(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        registerListener(this);
    }

    public void onLoginStart(Player p) {
        waitTick.put(p.getUniqueId(), 0);
        if (!inCaptcha.contains(p.getUniqueId()))
            inCaptcha.add(p.getUniqueId());
        getServer().getScheduler().runTaskLater(getPlugin(),
                () -> p.openInventory(new CaptchaMenu()), 1);
    }

    public void onLoginSuccess(Player p) {
        waitTick.remove(p.getUniqueId());
        inCaptcha.remove(p.getUniqueId());
    }

    @EventHandler
    public void onTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        for (Player ps : getServer().getOnlinePlayers()) {
            if (waitTick.containsKey(ps.getUniqueId())) {
                int currentTick = waitTick.get(ps.getUniqueId());
                if (currentTick < 20) {
                    if (!inCaptcha.contains(ps.getUniqueId())) {
                        Profile profile = Profile.getProfile(ps);
                        if (profile != null) {
                            if (profile.getData(DataType.ACCOUNT_PASSWORD).getAsString().isEmpty()) {
                                ps.sendMessage("§cUsage: /register <password>");
                            } else {
                                ps.sendMessage("§cUsage: /login <password>");
                            }
                        }
                    }
                } else {
                    ps.kickPlayer("§cVocê demorou para se autenticar.\nwww.lostmc.com.br");
                }
                waitTick.put(ps.getUniqueId(), currentTick + 1);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (inCaptcha.contains(event.getPlayer().getUniqueId())) {
            if (event.getInventory().getName().equals(MENU_NAME)) {
                getServer().getScheduler().runTaskLater(getPlugin(),
                        () -> event.getPlayer().openInventory(event.getInventory()), 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        waitTick.remove(event.getPlayer().getUniqueId());
        inCaptcha.remove(event.getPlayer());
    }

    public void onDisable() {
        waitTick.clear();
        inCaptcha.clear();
    }

    private class CaptchaMenu extends MenuInventory {

        public CaptchaMenu() {
            super(6 * 9, MENU_NAME);
            for (int i = 0; i < getSize(); i++)
                setItem(i, invalidItem, new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        p.kickPlayer("§cVocê falhou no captcha! Tente novamente.");
                    }
                });
            getServer().getScheduler().runTaskLater(getPlugin(),
                    () -> setItem(Commons.getRandom().nextInt(getSize() - 1), validItem,
                            new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            inCaptcha.remove(p.getUniqueId());
                            p.closeInventory();
                        }
                    }), 30);
        }
    }
}
