package com.lostmc.bukkit.api.cooldown.types;

import org.bukkit.inventory.ItemStack;

public class ItemCooldown extends Cooldown {

    private ItemStack item;
    private boolean selected;

    public ItemCooldown(ItemStack item, String name, Long duration) {
        super(name, duration);
        this.item = item;
    }
    
    public ItemStack getItem() {
    	return item;
    }
    
    public boolean isSelected() {
    	return selected;
    }
    
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
}
