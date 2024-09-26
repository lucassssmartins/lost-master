package com.lostmc.lobby;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.core.profile.Profile;
import com.lostmc.lobby.controller.LobbyController;
import com.lostmc.lobby.gamer.Gamer;
import com.lostmc.lobby.npc.NonPlayerController;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class Lobby extends BukkitPlugin {

    public Lobby() {
        super();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        getControl().enableController(LobbyController.class);
        getControl().enableController(ProxiedServerController.class);

        getListenerLoader().loadListeners("com.lostmc.lobby.listener");
        getCommandLoader().loadCommands("com.lostmc.lobby.command");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        for (Player p : getServer().getOnlinePlayers()) {
            removeGadgets(Profile.getProfile(p).getResource(Gamer.class));
        }
    }

    @EventHandler
    public void onWorld(WorldLoadEvent event) {
        super.onWorld(event);
        if (getControl().getController(NonPlayerController.class) == null) {
            getControl().enableController(NonPlayerController.class);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        world.setThundering(false);
        world.setStorm(false);
        world.setWeatherDuration(1000000000);
        world.setTime(6000);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setDifficulty(Difficulty.EASY);
        world.setGameRuleValue("doMobSpawning", "false");
    }

    @Override
    public void onProfileLoad(Profile profile) {
        profile.addResource(Gamer.class, new Gamer(profile));
    }

    @Override
    public void onProfileUnload(Profile profile) {
        Gamer gamer = profile.getResource(Gamer.class);
        removeGadgets(gamer);
    }

    private void removeGadgets(Gamer gamer) {
        gamer.getGadgets().forEach(gadget -> gadget.remove());
    }
}
