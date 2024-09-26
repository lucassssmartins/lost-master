package com.lostmc.bungee.manager;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.core.Commons;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.SilentPunishment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SilentManager extends ProxyManager {

    private Map<UUID, SilentPunishment> silents = new HashMap<>();

    public SilentManager(ProxyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {

    }

    public SilentPunishment getSilent(UUID id) {
        return silents.get(id);
    }

    public void silent(UUID id, SilentPunishment silent) {
        silents.put(id, silent);
    }

    public void loadSilent(UUID id) {
        try {
            for (Punishment punishment : Commons.getStorageCommon().getPunishmentStorage()
                    .getPlayerPunishments(id, Punishment.Type.SILENT))
                if (punishment instanceof SilentPunishment)
                    silents.put(id, (SilentPunishment) punishment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSilent(UUID id) {
        silents.remove(id);
    }

    @Override
    public void onDisable() {

    }
}
