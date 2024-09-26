package com.lostmc.bukkit.api.cooldown.event;

import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class CooldownFinishEvent extends CooldownEvent {

    public CooldownFinishEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
