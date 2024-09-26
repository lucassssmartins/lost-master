package com.lostmc.pvp.warp;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.core.utils.ClassGetter;
import com.lostmc.pvp.warp.registry.SpawnWarp;
import com.lostmc.pvp.warp.registry.system.FightLoc1Warp;
import com.lostmc.pvp.warp.registry.system.FightLoc2Warp;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.lostmc.pvp.PvP.PLUGIN_FILE;

public class WarpController extends Controller {

    private List<Warp> warps = new ArrayList<>();

    public WarpController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        for (Class<?> clazz : ClassGetter.getClassesForPackageByFile(PLUGIN_FILE,
                "com.lostmc.pvp.warp.registry")) {
            if (!Warp.class.isAssignableFrom(clazz))
                continue;
            try {
                loadWarp((Warp) clazz.getConstructor(WarpController.class).newInstance(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // SYSTEM READ ONLY
        loadWarp(new FightLoc1Warp(this));
        loadWarp(new FightLoc2Warp(this));
    }

    @Override
    public void onDisable() {
        this.warps.clear();
        this.warps = null;
    }

    public synchronized void loadWarp(Warp warp) {
        FileConfiguration config = getPlugin().getConfig();
        String key = "pvp.warps." + warp.getName().toLowerCase();
        if (config.contains(key))
            warp.setLocation(Commons.getGson().fromJson(config.getString(key),
                    ILocation.class).toLocation(getServer().getWorlds().get(0)));
        if (!(warp instanceof FightLoc1Warp) && !(warp instanceof FightLoc2Warp))
            registerListener(warp);
        this.warps.add(warp);
    }

    public void saveWarpLocation(Warp warp) {
        FileConfiguration config = getPlugin().getConfig();
        Location spawn = warp.getLocation();

        String key = "pvp.warps." + warp.getName().toLowerCase();
        config.set(key, Commons.getGson().toJson(new ILocation(spawn)));

        getPlugin().saveConfig();
    }

    public int getOnlineCount() {
        int count = 0;
        for (Player ps : getServer().getOnlinePlayers()) {
            if (getControl().getController(VanishController.class).isVanished(ps))
                continue;
            ++count;
        }
        return count;
    }

    public int getPlaying() {
        int playing = 0;
        for (Warp warp : this.warps) {
            if (warp instanceof SpawnWarp)
                continue;
            playing += warp.getPlayerCount();
        }
        return playing;
    }

    public Warp searchWarp(String name) {
        return this.warps.stream().filter(w -> w.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T> T getWarpByClass(Class<T> clazz) {
       return clazz.cast(this.warps.stream().filter(w -> w.getClass().equals(clazz)).findFirst().orElse(null));
    }
}
