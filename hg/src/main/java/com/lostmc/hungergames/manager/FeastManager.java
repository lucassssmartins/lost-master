package com.lostmc.hungergames.manager;

import com.lostmc.core.utils.DateUtils;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.structure.Feast;
import com.lostmc.hungergames.structure.items.FeastType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FeastManager extends Management {

    @Getter
    @Setter
    private boolean bonusFeast = true, bonusFeastSpawned = false;
    @Getter
    @Setter
    private boolean miniFeast = true;
    @Getter
    private int miniFeastCount = 0;
    private List<Feast> pendingFeasts = new ArrayList<>();

    public FeastManager(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        addFeast(new Feast("Main Feast", 25, 150, FeastType.DEFAULT, 300));
    }

    public void incrementMiniFeastCount() {
        ++miniFeastCount;
    }

    public void addFeast(Feast feast) {
        pendingFeasts.add(feast);
    }

    public Feast getFeast(int id) {
        return pendingFeasts.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public Feast getActualFeast() {
        for (Feast pending : pendingFeasts)
            return pending;
        return null;
    }

    public void onTimer(int timer) {
        Iterator<Feast> it = pendingFeasts.iterator();
        while (it.hasNext()) {
            Feast next = it.next();
            if (next.isDefault()) {
                if (next.isSpawned() || timer >= HungerGamesMode.FEAST_SPAWN - next.getCounter()) {
                    if (!next.isChestsSpawned()) {
                        if (next.getCounter() > 0) {
                            if (!next.isSpawned())
                                next.spawn();
                            if ((next.getCounter() % 60 == 0 || (next.getCounter() < 60
                                    && (next.getCounter() % 15 == 0 || next.getCounter() == 10
                                    || next.getCounter() <= 5)))) {
                                Location loc = next.getCentralLocation();
                                Bukkit.broadcastMessage("§cO feast principal irá spawnar em (" + loc.getBlockX()
                                        + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") em "
                                        + DateUtils.formatDifference(next.getCounter()));
                            }
                        } else {
                            next.spawnChests();
                            Location loc = next.getCentralLocation();
                            Bukkit.broadcastMessage("§cO feast principal spawnou em (" + loc.getBlockX()
                                    + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
                        }
                        next.count();
                    }
                }
            } else if (!next.isChestsSpawned()) {
                if (next.getCounter() > 0) {
                    if (!next.isSpawned())
                        next.spawn();
                    if ((next.getCounter() % 60 == 0 || (next.getCounter() < 60 && (next.getCounter()
                            % 15 == 0 || next.getCounter() == 10 || next.getCounter() <= 5)))) {
                        Location loc = next.getCentralLocation();
                        Bukkit.broadcastMessage("§cO feast " + next.getName() + " #" + next.getId() +
                                " irá spawnar em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", "
                                + loc.getBlockZ() + ") em "
                                + DateUtils.formatDifference(next.getCounter()));
                    }
                } else {
                    next.spawnChests();
                    Location loc = next.getCentralLocation();
                    Bukkit.broadcastMessage("§cO feast " + next.getName() + " #" + next.getId()
                            + " spawnou em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", "
                            + loc.getBlockZ() + ")");
                }
                next.count();
            }
        }
    }

    public void forceDefaultFeast() {
        Feast def = pendingFeasts.stream().filter(e -> e.isDefault()).findFirst()
                .orElse(null);
        if (def != null) {
            if (!def.isSpawned())
                def.spawn();
            if (!def.isChestsSpawned())
                def.spawnChests();
            Location loc = def.getCentralLocation();
            Bukkit.broadcastMessage("§cO feast principal spawnou em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
                    ")");
        }
        pendingFeasts.removeIf(f -> f.isDefault());
    }

    public void spawnBonusFeast() {
        Feast bonusFeast = new Feast("Bônus Feast", 20, 500, FeastType.DEFAULT, 0);
        bonusFeast.spawn();
        bonusFeast.spawnChests();
    }

    public void cancelFeast(int id) {
        pendingFeasts.removeIf(e -> e.getId() == id);
    }

    public List<Feast> getPendingFeasts() {
        return Collections.unmodifiableList(pendingFeasts);
    }

    @Override
    public void onDisable() {

    }
}
