package com.lostmc.pvp.warp.registry.system;

import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import org.bukkit.entity.Player;

public class FightLoc1Warp extends Warp {

    public FightLoc1Warp(WarpController controller) {
        super(controller);
        setName("1v1loc1");
    }

    @Override
    public void onPlayerJoin(Player player) {

    }

    @Override
    public void onProtectionLost(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }
}
