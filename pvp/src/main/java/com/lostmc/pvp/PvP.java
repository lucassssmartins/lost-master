package com.lostmc.pvp;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GameMode;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.ability.KitController;
import com.lostmc.pvp.ability.registry.controllers.GladiatorFightController;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.npc.NonPlayerController;
import com.lostmc.pvp.util.ItemManager;
import com.lostmc.pvp.warp.WarpController;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;

public class PvP extends GamePlugin {

    public static File PLUGIN_FILE;

    @Getter
    private GladiatorFightController gladiatorFightController = new GladiatorFightController();
    @Getter
    private ItemManager itemManager = new ItemManager();
    private boolean worldLoaded = false;

    public PvP() {
        super();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        PLUGIN_FILE = getFile();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        getControl().enableController(KitController.class);

        getListenerLoader().loadListeners("com.lostmc.pvp.listener");
        getCommandLoader().loadCommands("com.lostmc.pvp.command");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        gladiatorFightController.stop();
    }

    @Override
    public GameMode loadGameMode() {
        return new PvPMode(this);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!getControl().getControllers().containsKey(WarpController.class))
            getControl().enableController(WarpController.class);
        World world = event.getWorld();
        world.setThundering(false);
        world.setStorm(false);
        world.setWeatherDuration(1000000000);
        world.setTime(6000);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRuleValue("doMobSpawning", "false");
    }

    @EventHandler
    public void onWorld(WorldLoadEvent event) {
        if (!worldLoaded) {
            worldLoaded = true;
            getServer().getScheduler().runTaskLater(this, () -> {
                super.onWorld(event);
                getControl().enableController(NonPlayerController.class);
            }, 4L);
        }
    }

    @Override
    public void onProfileLoad(Profile profile) {
        profile.addResource(Gamer.class, new PvPGamer(profile.getUniqueId(), profile.getName()));
    }

    @Override
    public void onProfileUnload(Profile profile) {

    }
}
