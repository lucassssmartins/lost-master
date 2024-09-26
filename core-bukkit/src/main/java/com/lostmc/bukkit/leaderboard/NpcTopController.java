package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.api.line.TextLine;
import com.lostmc.bukkit.npc.NpcController;
import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.api.skin.Skin;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.division.Division;
import com.lostmc.core.profile.division.DivisionHandler;
import com.lostmc.core.property.IProperty;
import com.lostmc.core.property.IPropertyGetter;
import com.lostmc.core.property.SkinSource;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class NpcTopController extends Controller implements Listener {

    private final Map<NpcTopType, NpcTop> topNPCs = new HashMap<>();

    public NpcTopController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        for (NpcTopType topType : NpcTopType.values()) {
            if (topType == NpcTopType.HG_ELO) {
                topNPCs.put(topType, new NpcTopHGElo(this));
            } else if (topType == NpcTopType.HG_KILLS) {
                topNPCs.put(topType, new NpcTopHGKills(this));
            } else if (topType == NpcTopType.HG_WINS) {
                topNPCs.put(topType, new NpcTopHGWins(this));
            } else if (topType == NpcTopType.PVP_1V1_ELO) {
                topNPCs.put(topType, new NpcTop1V1Elo(this));
            }
        }
        getServer().getScheduler().runTaskAsynchronously(getPlugin(),
                () -> topNPCs.values().forEach(e -> e.start()));
        registerListener(this);
    }

    @EventHandler
    public void onTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % (1200 * 5) != 0)
            return;
        getServer().getScheduler().runTaskAsynchronously(getPlugin(),
                () -> topNPCs.values().forEach(e -> e.onUpdate()));
    }

    public void saveLocation(NpcTopType type, Location l, int i) {
        NpcTop topNPC = topNPCs.get(type);
        if (topNPC != null) {
            getServer().getScheduler().runTaskAsynchronously(getPlugin(),
                    () -> topNPC.saveLocation(l, i));
        }
    }

    @Override
    public void onDisable() {

    }
}
