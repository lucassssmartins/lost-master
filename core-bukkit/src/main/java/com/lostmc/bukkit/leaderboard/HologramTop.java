package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.api.handler.TouchHandler;
import com.lostmc.bukkit.hologram.api.line.TextLine;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.division.Division;
import com.lostmc.core.profile.division.DivisionHandler;
import com.lostmc.core.profile.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class HologramTop implements Listener {

    private Controller controller;
    private Leaderboard leadeboard;
    private Map<UUID, Integer> selectedPage = new HashMap<>();
    private Map<UUID, Hologram> perPlayerHolograms = new HashMap<>();
    private Location location;
    private HologramTopType topType;

    public HologramTop(Controller controller, DataType dataType, int size, HologramTopType topType) {
        this.controller = controller;
        this.leadeboard = new Leaderboard(dataType, size);
        this.topType = topType;
    }

    public void start() {
        leadeboard.update();
        if (controller.getPlugin().getConfig().isSet("leaderboard.hologram-" + topType.toString()
                + ".location")) {
            location = Commons.getGson().fromJson(controller.getPlugin().getConfig()
                            .getString("leaderboard.hologram-" + topType.toString() + ".location"),
                    ILocation.class).toLocation(Bukkit.getServer().getWorlds().get(0));
        }
        for (Player ps : Bukkit.getOnlinePlayers()) {
            updateHolograms(ps);
        }
        controller.registerListener(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (location != null) {
            updateHolograms(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (hasHologram(event.getPlayer())) {
            Hologram hl = perPlayerHolograms.remove(event.getPlayer().getUniqueId());
            hl.delete();
            selectedPage.remove(event.getPlayer().getUniqueId());
        }
    }

    public boolean hasHologram(Player p) {
        return perPlayerHolograms.containsKey(p.getUniqueId());
    }

    public int getSelectedPage(Player p) {
        return selectedPage.computeIfAbsent(p.getUniqueId(), v -> 1);
    }

    public Hologram getHologram(Player p) {
        return perPlayerHolograms.get(p.getUniqueId());
    }

    public void onUpdate() {
        leadeboard.update();
        for (Player ps : Bukkit.getOnlinePlayers()) {
            updateHolograms(ps);
        }
    }

    private void spawnHolograms(Player p) {
        if (this.location != null) {
            Hologram hl = controller.getControl().getController(HologramManager.class)
                    .createHologram(controller.getPlugin(), this.location);

            perPlayerHolograms.put(p.getUniqueId(), hl);

            hl.getVisibilityManager().setVisibleByDefault(false);
            hl.getVisibilityManager().showTo(p);

            TouchHandler touchHandler = paramPlayer -> {
                int page = getSelectedPage(paramPlayer) + 1;
                if (page > 10)
                    page = 1;
                selectedPage.put(paramPlayer.getUniqueId(), page);
                updateHolograms(paramPlayer);
            };

            hl.appendTextLine("§6§lClique para ver mais").setTouchHandler(touchHandler);

            int page = getSelectedPage(p);
            int row = 10 * page;
            int lines = 10;
            while (lines > 0) {
                Profile profile = leadeboard.getTopPlayers().get(row);
                if (profile != null) {
                    Tag tag = Tag.fromRank(profile.getRank());
                    hl.appendTextLine("§e" + row + ". §" + tag.getColorId() + profile.getName()
                            + format(profile)).setTouchHandler(touchHandler);
                } else {
                    hl.appendTextLine("§e" + row + ". §7" + "..."
                            + " - No Description.").setTouchHandler(touchHandler);
                }
                --lines;
                --row;
            }
            hl.appendTextLine("§b§lTOP 100 §e§l" + topType.getName() + " §7(" + getSelectedPage(p) + "/" + "10)")
                    .setTouchHandler(touchHandler);
        }
    }

    private String format(Profile profile) {
        if (topType == HologramTopType.TOP_1V1_ELO) {
            DivisionHandler handler = DivisionHandler.getInstance();
            int elo = profile.getData(DataType.FIGHT_1V1_ELO).getAsInt();
            Division division = handler.getDivisionByElo(elo);
            return " §7- " + division.getColor() + division.getName()
                    + " §7ELO: §e" + String.format("%,d", elo);
        } else if (topType == HologramTopType.TOP_COINS) {
            int elo = profile.getData(DataType.COINS).getAsInt();
            return " §7- §7Coins: §e" + String.format("%,d", elo);
        } else if (topType == HologramTopType.TOP_HG_ELO) {
            DivisionHandler handler = DivisionHandler.getInstance();
            int elo = profile.getData(DataType.HG_ELO).getAsInt();
            Division division = handler.getDivisionByElo(elo);
            return " §7- " + division.getColor() + division.getName()
                    + " §7ELO: §e" + String.format("%,d", elo);
        } else if (topType == HologramTopType.TOP_HG_KILLS) {
            int elo = profile.getData(DataType.HG_TOTAL_KILLS).getAsInt();
            return " §7- §7Kills: §e" + String.format("%,d", elo);
        } else if (topType == HologramTopType.TOP_HG_WINS) {
            int elo = profile.getData(DataType.HG_TOTAL_WINS).getAsInt();
            return " §7- §7Wins: §e" + String.format("%,d", elo);
        }
        return " §7- No description.";
    }

    public void updateHolograms(Player p) {
        Bukkit.getScheduler().runTask(controller.getPlugin(), () -> {
            Hologram hl = getHologram(p);
            if (hl == null) {
                if (location == null)
                    return;
                spawnHolograms(p);
                return;
            }
            int row = 10 * getSelectedPage(p);
            int line = 1;
            while (line <= 10) {
                Profile profile = leadeboard.getTopPlayers().get(row);
                if (profile != null) {
                    Tag tag = Tag.fromRank(profile.getRank());
                    ((TextLine) hl.getLine(line)).setText("§e" + row + ". §" + tag.getColorId()
                            + profile.getName() + format(profile));
                } else {
                    ((TextLine) hl.getLine(line)).setText("§e" + row + ". §7" + "..."
                            + " - No Description.");
                }
                ++line;
                --row;
            }

            ((TextLine) hl.getLine(11)).setText("§b§lTOP 100 §e§l" + topType.getName()
                    + " §7(" + getSelectedPage(p) + "/" + "10)");
        });
    }

    public void removeHolograms(Player p) {
        Hologram hologram = perPlayerHolograms.remove(p.getUniqueId());
        if (hologram != null) {
            hologram.delete();
        }
    }

    public void saveLocation(Location location) {
        controller.getPlugin().getConfig().set("leaderboard.hologram-" + topType.toString() + ".location",
                Commons.getGson().toJson(new ILocation(this.location = location)));
        controller.getPlugin().saveConfig();
        for (Hologram hl : perPlayerHolograms.values())
            hl.teleport(location);
        for (Player ps : Bukkit.getOnlinePlayers()) {
            updateHolograms(ps);
        }
    }
}
