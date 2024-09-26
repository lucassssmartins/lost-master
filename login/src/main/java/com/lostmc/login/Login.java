package com.lostmc.login;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.core.profile.Profile;
import com.lostmc.login.manager.LoginManager;

public class Login extends BukkitPlugin {

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getControl().enableController(LoginManager.class);
        getListenerLoader().loadListeners("com.lostmc.login.listener");
        getCommandLoader().loadCommands("com.lostmc.login.command");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onProfileLoad(Profile profile) {

    }

    @Override
    public void onProfileUnload(Profile profile) {

    }
}
