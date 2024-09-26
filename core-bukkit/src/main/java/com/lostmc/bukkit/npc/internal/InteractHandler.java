package com.lostmc.bukkit.npc.internal;

import com.lostmc.bukkit.npc.api.events.NPCInteractEvent;
import org.bukkit.entity.Player;

public interface InteractHandler {

    void onInteract(Player player, NPCInteractEvent.ClickType type);
}
