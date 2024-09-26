package com.lostmc.pvp.util;

import com.lostmc.game.constructor.Kit;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.ability.KitController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemManager {

    public boolean isAbilityItem(ItemStack item) {
        for (Kit ability : PvP.getControl().getController(KitController.class).getKits()) {
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
            if (stack.getType() == Material.COMPASS)
                continue;
            if (isAbilityItem(stack))
                continue;
            if (stack.getType().toString().contains("_SWORD")) {
                continue;
            }
            player.getWorld().dropItem(location, stack);
        }
        for (ItemStack stack : player.getInventory().getArmorContents()) {
            if (stack == null || stack.getType() == Material.AIR)
                continue;
            player.getWorld().dropItem(location, stack);
        }
    }
}
