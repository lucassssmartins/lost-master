/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.listeners.*;
import com.lostmc.bukkit.npc.nms.NPC_v1_8_R3;

import java.util.logging.Level;

public final class NpcController extends Controller {

    private double autoHideDistance = 50.0;

    public NpcController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        registerListener(new PlayerListener(this));
        registerListener(new ChunkListener(this));
        registerListener(new PeriodicMoveListener(this,
                NPCLibOptions.MovementHandling.playerMoveEvent().updateInterval));
        new PacketListener().start(this);
    }

    public void setAutoHideDistance(double autoHideDistance) {
        this.autoHideDistance = autoHideDistance;
    }

    public double getAutoHideDistance() {
        return autoHideDistance;
    }
    public NPC createNPC() {
        try {
            return new NPC_v1_8_R3(this);
        } catch (Exception exception) {
            getPlugin().getLogger().log(Level.INFO, "Failed to create NPC", exception);
        }

        return null;
    }

    @Override
    public void onDisable() {

    }
}
