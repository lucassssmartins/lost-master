package com.lostmc.hungergames.structure.items;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemGenerator {

    private static Random r = new Random();

    public static void generateToChest(Chest chest, FeastType feastType) {
        Inventory inv = chest.getBlockInventory();
        List<ItemStack> items = generate(feastType);
        Collections.shuffle(items);
        inv.clear();
        addChestItems(chest, items);
    }

    private static void addChestItems(Chest chest, List<ItemStack> stacks) {
        Map<Material, Amount> map = new HashMap<>();
        Inventory inv = chest.getBlockInventory();
        for (int i = 0; i < stacks.size(); i++) {
            if (r.nextInt(100) < 50) {
                ItemStack item = stacks.get(i);
                if (item.getType().toString().contains("DIAMOND_")) {
                    if (map.computeIfAbsent(item.getType(), v -> new Amount()).i++
                            > 1) {
                        continue;
                    }
                }
                inv.setItem(r.nextInt(inv.getSize()), stacks.get(i));
            }
        }
        map.clear();
        chest.update();
    }

    private static List<ItemStack> generate(FeastType feastType) {
        List<ItemStack> items = new ArrayList<>();
        switch (feastType) {
            case MINI: {
                if (r.nextBoolean())
                    items.add(generate(Material.DIAMOND, 2, 1));
                if (!r.nextBoolean())
                    items.add(generate(Material.IRON_INGOT, 4, 2));
                items.add(generate(Material.BREAD, 10, 5));
                items.add(generate(Material.INK_SACK, (short) 3, 10, 6));
                items.add(generate(Material.MUSHROOM_SOUP, 5, 3));
                items.add(generate(Material.LEATHER_HELMET, 0, 0));
                items.add(generate(Material.LEATHER_CHESTPLATE, 0, 0));
                items.add(generate(Material.LEATHER_LEGGINGS, 0, 0));
                items.add(generate(Material.WEB, 4, 2));
                items.add(generate(Material.TNT, 5, 2));
                items.add(generate(Material.FLINT_AND_STEEL, 5, 2));
                items.add(generate(Material.EXP_BOTTLE, 3, 1));
                items.add(generate(Material.INK_SACK, (short) 4, 24, 12));
                items.add(generate(Material.POTION, (short) 16386, 0, 0));
                items.add(generate(Material.POTION, (short) 16388, 0, 0));
                items.add(generate(Material.POTION, (short) 16394, 0, 0));
                items.add(generate(Material.POTION, (short) 16396, 0, 0));
                break;
            }
            case INSANE: {
                items.add(generate(Material.DIAMOND, 3, 2));
                items.add(generate(Material.DIAMOND_SWORD, 3, 2));
                items.add(generate(Material.DIAMOND_HELMET, 3, 2));
                items.add(generate(Material.DIAMOND_CHESTPLATE, 3, 2));
                items.add(generate(Material.DIAMOND_LEGGINGS, 3, 2));
                items.add(generate(Material.DIAMOND_BOOTS, 3, 2));
                items.add(generate(Material.IRON_HELMET, 3, 2));
                items.add(generate(Material.IRON_CHESTPLATE, 3, 2));
                items.add(generate(Material.IRON_LEGGINGS, 3, 2));
                items.add(generate(Material.IRON_BOOTS, 3, 2));
                items.add(generate(Material.COOKED_BEEF, 16, 4));
                items.add(generate(Material.COOKED_CHICKEN, 12, 8));
                items.add(generate(Material.BREAD, 20, 10));
                items.add(generate(Material.WATER_BUCKET, 3, 2));
                items.add(generate(Material.LAVA_BUCKET, 3, 2));
                items.add(generate(Material.MUSHROOM_SOUP, 15, 5));
                items.add(generate(Material.WEB, 32, 16));
                items.add(generate(Material.ENDER_PEARL, 20, 10));
                items.add(generate(Material.FLINT_AND_STEEL, 6, 2));
                items.add(generate(Material.ARROW, 36, 12));
                items.add(generate(Material.BOW, 6, 1));
                items.add(generate(Material.EXP_BOTTLE, 16, 12));
                items.add(generate(Material.INK_SACK, (short) 4, 20, 10));
                items.add(generate(Material.GOLDEN_APPLE, 6, 3));
                items.add(generate(Material.POTION, (short) 16385, 0, 0));
                items.add(generate(Material.POTION, (short) 16386, 0, 0));
                items.add(generate(Material.POTION, (short) 16387, 0, 0));
                items.add(generate(Material.POTION, (short) 16388, 0, 0));
                items.add(generate(Material.POTION, (short) 16389, 0, 0));
                items.add(generate(Material.POTION, (short) 16394, 0, 0));
                items.add(generate(Material.POTION, (short) 16396, 0, 0));
                break;
            }
            case DEFAULT: {
                if (r.nextBoolean())
                    items.add(generate(Material.DIAMOND, 0, 0));
                if (!r.nextBoolean())
                    items.add(generate(Material.DIAMOND_SWORD, 0, 0));
                if (r.nextBoolean())
                    items.add(generate(Material.DIAMOND_HELMET, 0, 0));
                if (!r.nextBoolean())
                    items.add(generate(Material.DIAMOND_CHESTPLATE, 0, 0));
                if (r.nextBoolean())
                    items.add(generate(Material.DIAMOND_LEGGINGS, 0, 0));
                if (!r.nextBoolean())
                    items.add(generate(Material.DIAMOND_BOOTS, 0, 0));
                items.add(generate(Material.IRON_HELMET, 0, 0));
                items.add(generate(Material.IRON_CHESTPLATE, 0, 0));
                items.add(generate(Material.IRON_LEGGINGS, 0, 0));
                items.add(generate(Material.IRON_BOOTS, 0, 0));
                items.add(generate(Material.COOKED_BEEF, 16, 4));
                items.add(generate(Material.COOKED_CHICKEN, 12, 8));
                items.add(generate(Material.BREAD, 20, 10));
                items.add(generate(Material.WATER_BUCKET, 0, 0));
                items.add(generate(Material.LAVA_BUCKET, 0, 0));
                items.add(generate(Material.MUSHROOM_SOUP, 15, 5));
                items.add(generate(Material.WEB, 10, 5));
                items.add(generate(Material.ENDER_PEARL, 10, 5));
                items.add(generate(Material.FLINT_AND_STEEL, 6, 2));
                items.add(generate(Material.ARROW, 36, 12));
                items.add(generate(Material.BOW, 6, 1));
                items.add(generate(Material.EXP_BOTTLE, 6, 3));
                items.add(generate(Material.INK_SACK, (short) 4, 20, 10));
                items.add(generate(Material.GOLDEN_APPLE, 6, 3));
                items.add(generate(Material.POTION, (short) 16385, 0, 0));
                items.add(generate(Material.POTION, (short) 16386, 0, 0));
                items.add(generate(Material.POTION, (short) 16387, 0, 0));
                items.add(generate(Material.POTION, (short) 16388, 0, 0));
                items.add(generate(Material.POTION, (short) 16389, 0, 0));
                items.add(generate(Material.POTION, (short) 16394, 0, 0));
                items.add(generate(Material.POTION, (short) 16396, 0, 0));
                break;
            }
        }
        return items;
    }

    public static ItemStack generate(Material mat, int max, int min) {
        return generate(mat, (short) 0, max, min);
    }

    private static ItemStack generate(Material mat, short durability, int max, int min) {
        int amount = 1;
        if (max > 0)
            amount = r.nextInt(max);
        if (min > 0 && amount < min)
            amount = min;
        return new ItemStack(mat, amount, durability);
    }

    static class Amount {

        public int i;
    }
}
