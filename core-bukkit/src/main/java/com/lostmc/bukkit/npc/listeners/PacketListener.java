/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc.listeners;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.npc.api.events.NPCInteractEvent;
import com.lostmc.bukkit.npc.internal.NPCBase;
import com.lostmc.bukkit.npc.internal.NPCList;
import com.lostmc.bukkit.npc.NpcController;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListener {

    private Plugin plugin;

    public void start(NpcController instance) {
        this.plugin = instance.getPlugin();

        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(BukkitPlugin.getInstance(), PacketType.Play.Client.USE_ENTITY) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.isCancelled())
                            return;
                        event.setCancelled(handleInteractPacket(event.getPlayer(), event.getPacket()));
                    }

                });
    }

    private boolean handleInteractPacket(Player player, PacketContainer packet) {
        NPCBase npc = null;
        int packetEntityId = packet.getIntegers().read(0);

        for (NPCBase testNPC : NPCList.getAllNPCs()) {
            if (testNPC.isCreated() && testNPC.isShown(player) && testNPC.getEntityId() == packetEntityId) {
                npc = testNPC;
                break;
            }
        }

        if (npc == null) {
            // Default player, not doing magic with the packet.
            return false;
        }

        NPCInteractEvent.ClickType clickType = packet.getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK
                ? NPCInteractEvent.ClickType.LEFT_CLICK : NPCInteractEvent.ClickType.RIGHT_CLICK;

        if (npc.getInteractHandler() != null) {
            NPCBase finalNpc = npc;
            Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> finalNpc.getInteractHandler().onInteract(player, clickType));
            return true;
        }

        return false;
    }
}
