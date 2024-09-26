package com.lostmc.bukkit.hologram.api.line;

import com.lostmc.bukkit.hologram.api.handler.TouchHandler;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public interface TouchableLine extends HologramLine {

    void setTouchHandler(TouchHandler touchHandler);

    TouchHandler getTouchHandler();
}
