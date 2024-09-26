package com.lostmc.bukkit.event.server;

import com.lostmc.core.server.ProxiedServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerInfoUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private ProxiedServer localhost;

    public ServerInfoUpdateEvent(ProxiedServer localhost) {
        this.localhost = localhost;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
