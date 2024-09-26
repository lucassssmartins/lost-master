package com.lostmc.hungergames.util;

import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.manager.KitManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemManager {

    public boolean isAbilityItem(ItemStack item) {
        for (Kit ability : Management.getManagement(KitManager.class).getAllKits()) {
            if (ability.getItems() == null)
                continue;
            if (!Arrays.asList(ability.getItems()).contains(item))
                continue;
            return true;
        }
        return false;
    }

    public void dropItems(Player player, org.bukkit.Location location) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() == Material.AIR)
                continue;
            if (isAbilityItem(stack))
                continue;
            player.getWorld().dropItem(location, stack);
        }
        for (ItemStack stack : player.getInventory().getArmorContents()) {
            if (stack == null || stack.getType() == Material.AIR)
                continue;
            player.getWorld().dropItem(location, stack);
        }
    }
}
