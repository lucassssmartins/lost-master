/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc.nms;

import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.api.skin.Skin;
import com.lostmc.bukkit.npc.api.state.NPCAnimation;
import com.lostmc.bukkit.npc.api.state.NPCSlot;
import com.lostmc.bukkit.npc.internal.NPCBase;
import com.lostmc.bukkit.npc.nms.packets.*;
import com.lostmc.bukkit.utils.entity.EntityUtils;
import com.lostmc.bukkit.npc.NpcController;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class NPC_v1_8_R3 extends NPCBase {

    private PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn;
    private PacketPlayOutPlayerInfo packetPlayOutPlayerInfoAdd, packetPlayOutPlayerInfoRemove;
    private PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation;
    private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;

    public NPC_v1_8_R3(NpcController instance) {
        super(instance);
    }

    @Override
    public void createPackets() {

        PacketPlayOutPlayerInfoWrapper packetPlayOutPlayerInfoWrapper = new PacketPlayOutPlayerInfoWrapper();

        // Packets for spawning the NPC:
        this.packetPlayOutPlayerInfoAdd = packetPlayOutPlayerInfoWrapper
                .create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, gameProfile, name); // Second packet to send.

        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawnWrapper()
                .create(uuid, location, entityId); // Third packet to send.

        this.packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotationWrapper()
                .create(location, entityId); // Fourth packet to send.

        this.packetPlayOutPlayerInfoRemove = packetPlayOutPlayerInfoWrapper
                .create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, gameProfile, name); // Fifth packet to send (delayed).

        // Packet for destroying the NPC:
        this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityId); // First packet to send.
    }

    @Override
    public NPC setLocation(Location location) {
        if (!isCreated()) {
            this.location = location;
            return this;
        }
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawnWrapper()
                .create(uuid, location, entityId);
        this.packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotationWrapper()
                .create(location, entityId);
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(
                entityId, MathHelper.floor(location.getX() * 32.0D), MathHelper.floor(location.getY() * 32.0D),
                MathHelper.floor(location.getZ() * 32.0D), (byte) ((int) (location.getYaw() * 256.0F / 360.0F)),
                (byte) ((int) (location.getPitch() * 256.0F / 360.0F)), true);
        for (UUID shownUuid : this.getShown()) {
            Player player = Bukkit.getPlayer(shownUuid);
            if (player != null && isShown(player)) {
                PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
                playerConnection.sendPacket(packet);
                playerConnection.sendPacket(new PacketPlayOutEntityHeadRotationWrapper()
                        .create(location, entityId));
            }
        }
        this.location = location;
        return super.setLocation(location);
    }

    @Override
    public void sendShowPackets(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        playerConnection.sendPacket(packetPlayOutPlayerInfoAdd);
        playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
        playerConnection.sendPacket(packetPlayOutEntityHeadRotation);

        Bukkit.getScheduler().runTaskLater(instance.getPlugin(), () ->
                playerConnection.sendPacket(packetPlayOutPlayerInfoRemove), 85L);

        int batEntityId = EntityUtils.next();

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player,
                    buildSpawnBatPacket(batEntityId, location));

            PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
            setFieldValue(packet, "a", 0);
            setFieldValue(packet, "b", batEntityId);
            setFieldValue(packet, "c", this.entityId);

            playerConnection.sendPacket(buildAttachPacket(batEntityId, this.entityId));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static PacketPlayOutAttachEntity buildAttachPacket(int a, int b) {
        PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
        setFieldValue(packet, "a", 0);
        setFieldValue(packet, "b", a);
        setFieldValue(packet, "c", b);
        return packet;
    }

    private static PacketContainer buildSpawnBatPacket(int entityId, Location loc) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) (1 << 5));
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet.getIntegers().write(0, entityId);
        packet.getIntegers().write(1, 65);
        packet.getIntegers().write(2, floor(loc.getX() * 32D));
        packet.getIntegers().write(3, floor(loc.getY() * 32D));
        packet.getIntegers().write(4, floor(loc.getZ() * 32D));
        packet.getDataWatcherModifier().write(0, watcher);

        return packet;
    }

    private static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < (double) var2 ? var2 - 1 : var2;
    }

    @Override
    public void sendHidePackets(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        playerConnection.sendPacket(packetPlayOutEntityDestroy);
        playerConnection.sendPacket(packetPlayOutPlayerInfoRemove);
    }

    private static void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMetadataPacket(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadataWrapper().create(activeStates, entityId);

        playerConnection.sendPacket(packet);
    }

    @Override
    public void sendEquipmentPacket(Player player, NPCSlot slot, boolean auto) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        if (slot == NPCSlot.OFFHAND) {
            if (!auto) {
                throw new UnsupportedOperationException("Offhand is not supported on servers below 1.9");
            }
            return;
        }

        ItemStack item = getItem(slot);

        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityId, slot.getSlot(), CraftItemStack.asNMSCopy(item));
        playerConnection.sendPacket(packet);
    }

    @Override
    public void sendAnimationPacket(Player player, NPCAnimation animation) {
        if (animation == NPCAnimation.SWING_OFFHAND) {
            throw new IllegalArgumentException("Offhand Swing Animations are only available on 1.9 and up.");
        }

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutAnimation packet = new PacketPlayOutAnimationWrapper().create(animation, entityId);
        playerConnection.sendPacket(packet);
    }

    @Override
    public void updateSkin(Skin skin) {
        GameProfile newProfile = new GameProfile(uuid, name);
        newProfile.getProperties().get("textures").clear();
        newProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        this.packetPlayOutPlayerInfoAdd = new PacketPlayOutPlayerInfoWrapper().create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, newProfile, name);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!getShown().contains(player.getUniqueId()))
                continue;
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packetPlayOutPlayerInfoRemove);
            playerConnection.sendPacket(packetPlayOutEntityDestroy);

            playerConnection.sendPacket(packetPlayOutPlayerInfoAdd);
            playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
            playerConnection.sendPacket(new PacketPlayOutEntityHeadRotationWrapper()
                    .create(location, entityId));

            Bukkit.getScheduler().runTaskLater(instance.getPlugin(), () ->
                    playerConnection.sendPacket(packetPlayOutPlayerInfoRemove), 85L);

            int batEntityId = EntityUtils.next();

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player,
                        buildSpawnBatPacket(batEntityId, location));

                PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
                setFieldValue(packet, "a", 0);
                setFieldValue(packet, "b", batEntityId);
                setFieldValue(packet, "c", this.entityId);

                playerConnection.sendPacket(buildAttachPacket(batEntityId, this.entityId));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendHeadRotationPackets(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            Location npcLocation = getLocation();
            Vector dirBetweenLocations = location.toVector().subtract(npcLocation.toVector());

            npcLocation.setDirection(dirBetweenLocations);

            float yaw = npcLocation.getYaw();
            float pitch = npcLocation.getPitch();

            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
            connection.sendPacket(new PacketPlayOutEntityHeadRotationWrapper().create(npcLocation, entityId));
        }
    }
}
