package com.lostmc.bukkit.hologram.nms.interfaces;

import com.lostmc.bukkit.hologram.craft.line.CraftHologramLine;
import com.lostmc.bukkit.hologram.craft.line.CraftTouchSlimeLine;
import com.lostmc.bukkit.hologram.nms.entity.NMSArmorStand;
import com.lostmc.bukkit.hologram.nms.entity.NMSEntityBase;
import com.lostmc.bukkit.hologram.nms.entity.NMSSlime;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public interface NMSManager {

    void setup() throws Exception;

    NMSArmorStand spawnNMSArmorStand(World world, double x, double y, double z, CraftHologramLine line);

    NMSSlime spawnNMSSlime(World world, double x, double y, double z, CraftTouchSlimeLine line);

    boolean isNMSEntityBase(Entity entity);

    NMSEntityBase getNMSEntityBase(Entity entity);

    boolean hasChatHoverFeature();
}
