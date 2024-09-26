package com.lostmc.bukkit.api.cooldown.event;

import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class CooldownEvent extends PlayerEvent {

    private Cooldown cooldown;
    
    public CooldownEvent(Player player, Cooldown cooldown) {
    	super(player);
    	this.cooldown = cooldown;
    }
    
    public Cooldown getCooldown() {
    	return cooldown;
    }
}
