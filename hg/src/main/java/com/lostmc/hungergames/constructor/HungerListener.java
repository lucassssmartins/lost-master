package com.lostmc.hungergames.constructor;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.hungergames.HungerGames;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class HungerListener extends BukkitListener {

    public HungerGames getMain() {
        return (HungerGames) HungerGames.getInstance();
    }
}
