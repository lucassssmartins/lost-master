package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.listener.BukkitListener;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class EnchantingListener extends BukkitListener {

    private ItemStack lapis;

    public EnchantingListener() {
        Dye d = new Dye();
        d.setColor(DyeColor.BLUE);
        this.lapis = d.toItemStack();
        this.lapis.setAmount(3);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && event.getInventory() instanceof EnchantingInventory) {
            event.getInventory().setItem(1, null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getInventory() instanceof EnchantingInventory) {
            if (event.getSlot() == 1 && event.getInventory().getItem(1) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player && e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, this.lapis);
        }
    }
}
