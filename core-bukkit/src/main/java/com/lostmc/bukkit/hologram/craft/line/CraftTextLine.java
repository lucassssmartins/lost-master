package com.lostmc.bukkit.hologram.craft.line;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.handler.TouchHandler;
import com.lostmc.bukkit.hologram.api.line.TextLine;
import com.lostmc.bukkit.hologram.craft.CraftHologram;
import com.lostmc.bukkit.hologram.nms.entity.NMSEntityBase;
import com.lostmc.bukkit.hologram.nms.entity.NMSNameable;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public class CraftTextLine extends CraftTouchableLine implements TextLine {

    private String text;
    private NMSNameable nmsNameble;
    private NMSEntityBase nmsSkullVehicle;

    public CraftTextLine(CraftHologram parent, String text) {
        super(0.23D, parent);
        setText(text);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        if (this.nmsNameble != null)
            if (text != null && !text.isEmpty()) {
                this.nmsNameble.setCustomNameNMS(text);
            } else {
                this.nmsNameble.setCustomNameNMS("");
            }
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        if (this.nmsNameble != null) {
            Location loc = this.nmsNameble.getBukkitEntityNMS().getLocation();
            setTouchHandler(touchHandler, loc.getWorld(), loc.getX(), loc.getY() - -1.25D, loc.getZ());
        } else {
            setTouchHandler(touchHandler, null, 0.0D, 0.0D, 0.0D);
        }
    }

    public void spawn(World world, double x, double y, double z) {
        super.spawn(world, x, y, z);
        this.nmsNameble = BukkitPlugin.getControl().getController(HologramManager.class)
                .getNmsManager().spawnNMSArmorStand(world, x, y + -1.25D, z, this);
        if (this.text != null && !this.text.isEmpty())
            this.nmsNameble.setCustomNameNMS(this.text);
        this.nmsNameble.setLockTick(true);
    }

    public void despawn() {
        super.despawn();
        if (this.nmsSkullVehicle != null) {
            this.nmsSkullVehicle.killEntityNMS();
            this.nmsSkullVehicle = null;
        }
        if (this.nmsNameble != null) {
            this.nmsNameble.killEntityNMS();
            this.nmsNameble = null;
        }
    }

    public void teleport(double x, double y, double z) {
        super.teleport(x, y, z);
        if (this.nmsSkullVehicle != null)
            this.nmsSkullVehicle.setLocationNMS(x, y + 54.56D, z);
        if (this.nmsNameble != null)
            this.nmsNameble.setLocationNMS(x, y + -1.25D, z);
    }

    public int[] getEntitiesIDs() {
        if (isSpawned()) {
            if (this.nmsSkullVehicle != null) {
                if (this.touchSlime != null)
                    return ArrayUtils.addAll(new int[] { this.nmsNameble.getIdNMS(), this.nmsSkullVehicle.getIdNMS() }, this.touchSlime.getEntitiesIDs());
                return new int[] { this.nmsNameble.getIdNMS(), this.nmsSkullVehicle.getIdNMS() };
            }
            if (this.touchSlime != null)
                return ArrayUtils.add(this.touchSlime.getEntitiesIDs(), this.nmsNameble.getIdNMS());
            return new int[] { this.nmsNameble.getIdNMS() };
        }
        return new int[0];
    }

    public NMSNameable getNmsNameble() {
        return this.nmsNameble;
    }

    public NMSEntityBase getNmsSkullVehicle() {
        return this.nmsSkullVehicle;
    }

    public String toString() {
        return "CraftTextLine [text=" + this.text + "]";
    }
}
