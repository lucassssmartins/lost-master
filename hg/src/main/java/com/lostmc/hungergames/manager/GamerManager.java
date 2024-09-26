package com.lostmc.hungergames.manager;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GamerManager extends Management {

    private Map<UUID, HungerGamer> gamers;

    public GamerManager(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        gamers = new HashMap<>();
    }

    public Collection<HungerGamer> getAllGamers() {
        return this.gamers.values();
    }

    public Collection<HungerGamer> getAliveGamers() {
        return this.gamers.values().stream()
                .filter(g -> g.getGamerState() == HungerGamer.GamerState.ALIVE)
                .collect(Collectors.toList());
    }

    public Collection<HungerGamer> getSpectators() {
        return this.gamers.values().stream().filter(g -> g.getGamerState() == HungerGamer.GamerState.SPECTATOR)
                .collect(Collectors.toList());
    }

    public void loadGamer(Profile profile) {
        gamers.putIfAbsent(profile.getUniqueId(), new HungerGamer(profile.getUniqueId(), profile.getName()));
    }

    public HungerGamer getGamer(UUID id) {
        return gamers.get(id);
    }

    public void removeGamer(UUID id) {
        gamers.remove(id);
    }

    @Override
    public void onDisable() {
        gamers.clear();
        gamers = null;
    }
}
