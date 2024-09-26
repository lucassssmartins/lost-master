package com.lostmc.pvp;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GameMode;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.GameType;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.constructor.Gamer;
import org.bukkit.entity.Player;

public class PvPMode extends GameMode {

    public PvPMode(GamePlugin main) {
        super(main, true);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void startGame() {

    }

    @Override
    public CombatLog getCombatLog(Player p) {
        return Profile.getProfile(p).getResource(Gamer.class)
                .getCombatLog();
    }
}
