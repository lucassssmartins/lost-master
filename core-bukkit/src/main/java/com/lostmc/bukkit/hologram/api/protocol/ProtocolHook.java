package com.lostmc.bukkit.hologram.api.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.craft.CraftHologram;
import com.lostmc.bukkit.hologram.craft.line.CraftHologramLine;
import com.lostmc.bukkit.hologram.craft.line.CraftTextLine;
import com.lostmc.bukkit.hologram.craft.line.CraftTouchSlimeLine;
import com.lostmc.bukkit.hologram.craft.line.CraftTouchableLine;
import com.lostmc.bukkit.hologram.nms.entity.NMSEntityBase;
import com.lostmc.bukkit.hologram.nms.interfaces.NMSManager;
import com.lostmc.bukkit.hologram.util.Utils;
import com.lostmc.bukkit.protocol.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public class ProtocolHook {

    private static NMSManager nmsManager;

    public static boolean load(NMSManager nmsManager) {
        ProtocolHook.nmsManager = nmsManager;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(BukkitPlugin.getInstance(),
                        ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY_LIVING,
                        PacketType.Play.Server.SPAWN_ENTITY) {
                    public void onPacketSending(PacketEvent event) {
                        PacketContainer packet = event.getPacket();
                        if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
                            WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet);
                            if (!HologramManager.hasRegistry(spawnEntityPacket.getEntityID()))
                                return;
                            Entity entity = spawnEntityPacket.getEntity(event);
                            if (entity == null)
                                return;
                            CraftHologram hologram = getHologram(entity);
                            if (hologram == null)
                                return;
                            Player player = event.getPlayer();
                            if (!hologram.getVisibilityManager().isVisibleTo(player)) {
                                event.setCancelled(true);
                            }
                        } else if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY) {
                            WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(packet);
                            if (!HologramManager.hasRegistry(spawnEntityPacket.getEntityID()))
                                return;
                            Entity entity = spawnEntityPacket.getEntity(event);
                            if (entity == null)
                                return;
                            CraftHologram hologram = getHologram(entity);
                            if (hologram == null)
                                return;
                            Player player = event.getPlayer();
                            if (!hologram.getVisibilityManager().isVisibleTo(player)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
        return true;
    }

    public static void sendDestroyEntitiesPacket(Player player, CraftHologram hologram) {
        List<Integer> ids = Utils.newList();
        for (CraftHologramLine line : hologram.getLinesUnsafe()) {
            if (line.isSpawned()) {
                byte b;
                int i;
                int[] arrayOfInt;
                for (i = (arrayOfInt = line.getEntitiesIDs()).length, b = 0; b < i; ) {
                    int id = arrayOfInt[b];
                    ids.add(Integer.valueOf(id));
                    b++;
                }
            }
        }
        if (!ids.isEmpty()) {
            WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
            packet.setEntities(ids);
            packet.sendPacket(player);
        }
    }

    public static void sendCreateEntitiesPacket(Player player, CraftHologram hologram) {
        for (CraftHologramLine line : hologram.getLinesUnsafe()) {
            if (line.isSpawned()) {
                if (line instanceof CraftTextLine) {
                    CraftTextLine textLine = (CraftTextLine) line;
                    if (textLine.isSpawned()) {
                        AbstractPacket nameablePacket = new WrapperPlayServerSpawnEntityLiving(textLine.getNmsNameble().getBukkitEntityNMS());
                        nameablePacket.sendPacket(player);
                        if (textLine.getNmsSkullVehicle() != null) {
                            AbstractPacket vehiclePacket = new WrapperPlayServerSpawnEntity(textLine.getNmsSkullVehicle().getBukkitEntityNMS(), 66, 0);
                            vehiclePacket.sendPacket(player);
                            WrapperPlayServerAttachEntity attachPacket = new WrapperPlayServerAttachEntity();
                            attachPacket.setVehicleId(textLine.getNmsSkullVehicle().getIdNMS());
                            attachPacket.setEntityId(textLine.getNmsNameble().getIdNMS());
                            attachPacket.sendPacket(player);
                        }
                    }
                }
                CraftTouchableLine touchableLine = (CraftTouchableLine) line;
                if (touchableLine.isSpawned() && touchableLine.getTouchSlime() != null) {
                    CraftTouchSlimeLine touchSlime = touchableLine.getTouchSlime();
                    if (touchSlime.isSpawned()) {
                        AbstractPacket vehiclePacket = new WrapperPlayServerSpawnEntityLiving(
                                touchSlime.getNmsVehicle().getBukkitEntityNMS());
                        vehiclePacket.sendPacket(player);
                        AbstractPacket slimePacket = new WrapperPlayServerSpawnEntityLiving(
                                touchSlime.getNmsSlime().getBukkitEntityNMS());
                        slimePacket.sendPacket(player);
                        WrapperPlayServerAttachEntity attachPacket = new WrapperPlayServerAttachEntity();
                        attachPacket.setVehicleId(touchSlime.getNmsVehicle().getIdNMS());
                        attachPacket.setEntityId(touchSlime.getNmsSlime().getIdNMS());
                        attachPacket.sendPacket(player);
                    }
                }
            }
        }
    }

    private static boolean isHologramType(EntityType type) {
        return !(type != EntityType.HORSE && type != EntityType.WITHER_SKULL && type !=
                EntityType.DROPPED_ITEM && type != EntityType.SLIME && type != EntityType.ARMOR_STAND);
    }

    private static CraftHologram getHologram(Entity bukkitEntity) {
        NMSEntityBase entity = nmsManager.getNMSEntityBase(bukkitEntity);
        if (entity != null)
            return entity.getHologramLine().getParent();
        return null;
    }
}
