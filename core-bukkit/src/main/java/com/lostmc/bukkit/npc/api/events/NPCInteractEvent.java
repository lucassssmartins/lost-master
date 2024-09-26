/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc.api.events;

import com.lostmc.bukkit.npc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ClickType clickType;
    private final NPC npc;

    public NPCInteractEvent(Player player, ClickType clickType, NPC npc) {
        this.player = player;
        this.clickType = clickType;
        this.npc = npc;
    }

    public Player getWhoClicked() {
        return this.player;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public NPC getNPC() {
        return this.npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum ClickType {
        LEFT_CLICK, RIGHT_CLICK
    }
}
