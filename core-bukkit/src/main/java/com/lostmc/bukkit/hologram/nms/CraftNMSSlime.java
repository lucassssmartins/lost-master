package com.lostmc.bukkit.hologram.nms;

import java.util.Collection;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public class CraftNMSSlime extends CraftSlime {

    public CraftNMSSlime(CraftServer server, EntityNMSSlime entity) {
        super(server, entity);
    }

    public void remove() {}

    public boolean addPotionEffect(PotionEffect effect) {
        return false;
    }

    public boolean addPotionEffect(PotionEffect effect, boolean param) {
        return false;
    }

    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return false;
    }

    public void setRemoveWhenFarAway(boolean remove) {}

    public void setVelocity(Vector vel) {}

    public boolean teleport(Location loc) {
        return false;
    }

    public boolean teleport(Entity entity) {
        return false;
    }

    public boolean teleport(Location loc, PlayerTeleportEvent.TeleportCause cause) {
        return false;
    }

    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause cause) {
        return false;
    }

    public void setFireTicks(int ticks) {}

    public boolean setPassenger(Entity entity) {
        return false;
    }

    public boolean eject() {
        return false;
    }

    public boolean leaveVehicle() {
        return false;
    }

    public void playEffect(EntityEffect effect) {}

    public void setCustomName(String name) {}

    public void setCustomNameVisible(boolean flag) {}

    public void setSize(int size) {}
}
