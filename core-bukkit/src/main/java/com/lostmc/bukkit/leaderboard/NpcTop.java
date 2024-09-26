package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.control.Controller;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public abstract class NpcTop implements Listener {

    private final Controller controller;
    private final Leaderboard leaderboard;
    private final Map<Integer, NPC> npcs = new HashMap<>();
    private final Map<Integer, Hologram> holograms = new HashMap<>();
    private final Map<Integer, Location> locations = new HashMap<>();
    private final NpcTopType topType;

    public NpcTop(Controller controller, DataType dataType, int size, NpcTopType topType) {
        this.controller = controller;
        this.leaderboard = new Leaderboard(dataType, size);
        this.topType = topType;
    }

    public void start() {
        for (int i = 1; i <= 3; i++) {
            if (controller.getPlugin().getConfig().isSet("leaderboard.npc-top-" + topType.toString()
                    + i + ".location")) {
                locations.put(i, Commons.getGson().fromJson(
                        controller.getPlugin().getConfig()
                                .getString("leaderboard.npc-top-" + topType + i + ".location"),
                        ILocation.class).toLocation(Bukkit.getServer().getWorlds().get(0)));
            }
        }
        leaderboard.update();
        for (int i = 1; i <= 3; i++)
            updateTopNpc(i);
        controller.registerListener(this);
    }

    public void updateTopNpc(int i) {
        Profile profile = this.leaderboard.getTopPlayers().get(i);
        if (profile == null)
            return;
        if (!locations.containsKey(i))
            return;
        NPC npc = npcs.get(i);
        if (npc != null) {
            IProperty property;
            if (profile.getSkinSource() == null || profile.getSkinSource() == SkinSource.ACCOUNT)
                property = IPropertyGetter.getProperty(profile.getUniqueId().toString());
            else
                property = profile.getProperty();
            if (property != null)
                npc.updateSkin(new Skin(property.getValue(), property.getSignature()));
        } else {
            NpcController npcManager = BukkitPlugin.getControl().getController(NpcController.class);
            npc = npcManager.createNPC().setLocation(locations.get(i));

            npcs.put(i, npc);

            IProperty property;
            if (profile.getSkinSource() == null || profile.getSkinSource() == SkinSource.ACCOUNT)
                property = IPropertyGetter.getProperty(profile.getUniqueId().toString());
            else
                property = profile.getProperty();
            if (property != null)
                npc.setSkin(new Skin(property.getValue(), property.getSignature()));
            npc.create();

            NPC finalNpc = npc;
            controller.getControl().getServer().getOnlinePlayers().forEach(finalNpc::show);
        }

        if (!holograms.containsKey(i)) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    DivisionHandler divisionHandler = DivisionHandler.getInstance();
                    Division division = divisionHandler.getDivisionByElo(profile.getData(leaderboard.getDataType()).getAsInt());
                    String divisionName = division.getColor() + division.getName();

                    HologramManager manager = controller.getControl().getController(HologramManager.class);
                    Hologram hologram = manager.createHologram(controller.getPlugin(), locations.get(i).clone().add(0, 1.85, 0));
                    hologram.getVisibilityManager().setVisibleByDefault(true);
                    hologram.appendTextLine(divisionName);
                    hologram.appendTextLine(col0r(i).toString() + i + "º " + profile.getName());
                    holograms.put(i, hologram);
                }
            }.runTask(controller.getPlugin());
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    DivisionHandler divisionHandler = DivisionHandler.getInstance();
                    Division division = divisionHandler.getDivisionByElo(profile.getData(leaderboard.getDataType()).getAsInt());
                    String divisionName = division.getColor() + division.getName();

                    Hologram hologram = holograms.get(i);
                    ((TextLine) hologram.getLine(0)).setText(divisionName);
                    ((TextLine) hologram.getLine(1)).setText(col0r(i).toString() + i + "º " + profile.getName());
                }
            }.runTask(controller.getPlugin());
        }
    }

    public void updateHologramsPosition(int i) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Hologram hologram = holograms.get(i);
                if (hologram != null) {
                    hologram.teleport(locations.get(i).clone().add(0, 1.85, 0));
                }
            }
        }.runTask(BukkitPlugin.getInstance());
    }

    private ChatColor col0r(int row) {
        if (row == 1)
            return ChatColor.GREEN;
        else if (row == 2)
            return ChatColor.YELLOW;
        return ChatColor.RED;
    }

    @EventHandler
    public void onJoinListener(PlayerJoinEvent event) {
        npcs.values().forEach(npc -> npc.show(event.getPlayer()));
    }

    public void onUpdate() {
        leaderboard.update();
        for (int i = 1; i <= 3; i++) {
            updateTopNpc(i);
        }
    }

    public void saveLocation(Location location, int row) {
        locations.put(row, location);
        controller.getPlugin().getConfig().set("leaderboard.npc-top-" + topType.toString() + row + ".location",
                Commons.getGson().toJson(new ILocation(locations.get(row))));
        if (!npcs.containsKey(row))
            updateTopNpc(row);
        else {
            npcs.get(row).setLocation(location);
            updateHologramsPosition(row);
        }
        controller.getPlugin().saveConfig();
    }
}
