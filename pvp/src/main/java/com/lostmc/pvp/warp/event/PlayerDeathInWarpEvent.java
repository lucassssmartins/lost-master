package com.lostmc.pvp.warp.event;

import com.lostmc.pvp.warp.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class PlayerDeathInWarpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Warp warp;
    @Setter
    private boolean dropItems;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
