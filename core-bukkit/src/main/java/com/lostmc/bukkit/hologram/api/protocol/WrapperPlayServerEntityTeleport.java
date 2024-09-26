package com.lostmc.bukkit.hologram.api.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerEntityTeleport extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    public WrapperPlayServerEntityTeleport() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityTeleport(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve entity Id.
     *
     * @return The current EID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set entity Id.
     *
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity.
     *
     * @param world - the current world of the entity.
     * @return The entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity.
     *
     * @param event - the packet event.
     * @return The entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public double getX() {
        return this.handle.getIntegers().read(1) / 32.0D;
    }

    public void setX(double value) {
        handle.getIntegers().write(1, (int) Math.floor(value * 32.0D));
    }

    public double getY() {
        return this.handle.getIntegers().read(2) / 32.0D;
    }

    public void setY(double value) {
        handle.getIntegers().write(2, (int) Math.floor(value * 32.0D));
    }

    public double getZ() {
        return this.handle.getIntegers().read(3) / 32.0D;
    }

    public void setZ(double value) {
        handle.getIntegers().write(3, (int) Math.floor(value * 32.0D));
    }

    /**
     * Retrieve the yaw of the current entity.
     *
     * @return The current Yaw
     */
    public float getYaw() {
        return this.handle.getBytes().read(0) * 360.0F / 256.0F;
    }

    /**
     * Set the yaw of the current entity.
     *
     * @param value - new yaw.
     */
    public void setYaw(float value) {
        this.handle.getBytes().write(0, Byte.valueOf((byte)(int)(value * 256.0F / 360.0F)));
    }

    /**
     * Retrieve the pitch of the current entity.
     *
     * @return The current pitch
     */
    public float getPitch() {
        return this.handle.getBytes().read(1) * 360.0F / 256.0F;
    }

    /**
     * Set the pitch of the current entity.
     *
     * @param value - new pitch.
     */
    public void setPitch(float value) {
        this.handle.getBytes().write(1, Byte.valueOf((byte)(int)(value * 256.0F / 360.0F)));
    }

    public boolean getOnGround() {
        return handle.getBooleans().read(0);
    }

    public void setOnGround(boolean value) {
        handle.getBooleans().write(0, value);
    }
}
