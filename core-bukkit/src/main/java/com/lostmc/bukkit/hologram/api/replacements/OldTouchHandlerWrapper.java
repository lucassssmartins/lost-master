package com.lostmc.bukkit.hologram.api.replacements;

import com.lostmc.bukkit.hologram.api.handler.TouchHandler;
import com.lostmc.bukkit.hologram.craft.CraftHologram;
import org.bukkit.entity.Player;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public class OldTouchHandlerWrapper implements TouchHandler {

  public TouchHandler oldHandler;
  
  private CraftHologram hologram;
  
  public OldTouchHandlerWrapper(CraftHologram hologram, TouchHandler oldHandler) {
    this.hologram = hologram;
    this.oldHandler = oldHandler;
  }
  
  public void onTouch(Player player) {
    this.oldHandler.onTouch(player);
  }
}
