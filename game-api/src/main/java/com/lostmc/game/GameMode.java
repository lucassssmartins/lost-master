package com.lostmc.game;

import com.lostmc.game.constructor.CombatLog;
import lombok.Setter;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class GameMode {

    private GamePlugin main;
    private GameType gameType;
    @Setter
    private boolean doubleKit;

    public GameMode(GamePlugin main, boolean doubleKit) {
        this.main = main;
        this.gameType = main.getGameType();
        this.doubleKit = doubleKit;
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void startGame();

    public GamePlugin getMain() {
        return main;
    }

    public Server getServer() {
        return main.getServer();
    }

    public GameType getGameType() {
        return gameType;
    }

    public boolean isDoubleKit() {
        return doubleKit;
    }

    public abstract CombatLog getCombatLog(Player p);
}
