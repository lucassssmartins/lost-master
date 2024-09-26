package com.lostmc.hungergames.sidebar;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardModel;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public abstract class HungerSidebarModel extends ScoreboardModel {

    @Setter
    @Getter
    private static String sidebarTitle;
    protected List<String> perPlayer = new LinkedList<>();

    public HungerSidebarModel(Scoreboard scoreboard) {
        super(scoreboard);
        if (getSidebarTitle() == null) {
            ProxiedServer server = Commons.getProxyHandler().getLocal();
            String serverId = server.getId();
            boolean isDoubleKit = ((HungerGames) HungerGames.getInstance()).getGameMode().isDoubleKit();
            setSidebarTitle("§6§l" + (server.getServerType() == ServerType.HG_EVENT
                    ? "§6§lEVENTO" : (isDoubleKit ? "DOUBLEKIT" : "SINGLEKIT")
                    + " #" + serverId.replaceAll("[a-zA-Z0-]", "")));
        }
    }

    public boolean isVanished(Player p) {
        return HungerGames.getControl().getController(VanishController.class)
                .isVanished(p);
    }

    public HungerGames getMain() {
        return ((HungerGames) HungerGames.getInstance());
    }

    public HungerGamesMode getGameMode() {
       return (HungerGamesMode) getMain().getGameMode();
    }

    public String formatTime(int time) {
        if (time >= 3600) {
            int hours = (time / 3600), minutes = (time % 3600) / 60, seconds = (time % 3600) % 60;
            return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        } else {
            int minutes = (time / 60), seconds = (time % 60);
            return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        }
    }
}
