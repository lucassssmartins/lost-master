package com.lostmc.game;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import lombok.Getter;

@Getter
public abstract class GamePlugin extends BukkitPlugin {

    private GameType gameType;
    private GameMode gameMode;

    public GamePlugin() {
        super();
        setInstance(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        try {
            this.gameType = GameType.valueOf(getConfig().getString("game.type"));
        } catch (Exception e) {
            this.gameType = getDefaultGameType();
        }

        this.gameMode = loadGameMode();
        this.gameMode.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.gameMode.onEnable();
        getListenerLoader().loadListeners("com.lostmc.game.listener");
        getCommandLoader().loadCommands("com.lostmc.game.command");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.gameMode.onDisable();
    }

    public GameType getDefaultGameType() {
        return GameType.PVP;
    }

    public abstract GameMode loadGameMode();
}
