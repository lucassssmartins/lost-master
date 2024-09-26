package com.lostmc.pvp.ability.registry.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class GladiatorEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player gladiator;
    private Player target;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
