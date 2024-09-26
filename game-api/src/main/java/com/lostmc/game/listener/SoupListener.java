package com.lostmc.game.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class SoupListener extends BukkitListener {

    public SoupListener() {
        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CACTUS), new MaterialData(Material.BOWL)));
        newShapelessRecipe(soup,
                Arrays.asList(new MaterialData(Material.NETHER_STALK), new MaterialData(Material.BOWL)));
        newShapelessRecipe(soup,
                Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
        newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.SUGAR), new MaterialData(Material.BOWL)));
        newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.PUMPKIN_SEEDS),
                new MaterialData(Material.PUMPKIN_SEEDS), new MaterialData(Material.BOWL)));
        newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CARROT_ITEM),
                new MaterialData(Material.POTATO_ITEM), new MaterialData(Material.BOWL)));
    }

    public void newShapelessRecipe(ItemStack result, List<MaterialData> materials) {
        ShapelessRecipe recipe = new ShapelessRecipe(result);
        for (MaterialData mat : materials)
            recipe.addIngredient(mat);
        Bukkit.addRecipe(recipe);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSoup(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR)
            return;
        if (item.getType() == Material.MUSHROOM_SOUP) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.getHealth() < p.getMaxHealth() || p.getFoodLevel() < 20) {
                    int restores = 7;
                    event.setCancelled(true);
                    if (p.getHealth() < p.getMaxHealth())
                        if (p.getHealth() + restores <= p.getMaxHealth())
                            p.setHealth(p.getHealth() + restores);
                        else
                            p.setHealth(p.getMaxHealth());
                    else if (p.getFoodLevel() < 20)
                        if (p.getFoodLevel() + restores <= 20) {
                            p.setFoodLevel(p.getFoodLevel() + restores);
                            p.setSaturation(3);
                        } else {
                            p.setFoodLevel(20);
                            p.setSaturation(3);
                        }
                    item = new ItemStack(Material.BOWL);
                    p.setItemInHand(item);
                }
            }
        }
    }
}
