package com.lostmc.bukkit.hologram.api.line;

import com.lostmc.bukkit.hologram.api.Hologram;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public interface HologramLine {

    Hologram getParent();

    void removeLine();
}
