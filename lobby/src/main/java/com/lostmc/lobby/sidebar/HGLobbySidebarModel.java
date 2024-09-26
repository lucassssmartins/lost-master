package com.lostmc.lobby.sidebar;

import com.google.common.collect.Lists;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardModel;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.entity.Player;

import java.util.List;

public class HGLobbySidebarModel extends ScoreboardModel {

    private List<String> perPlayer = Lists.newLinkedList();

    public HGLobbySidebarModel(Scoreboard scoreboard) {
        super(scoreboard);
    }

    @Override
    public List<String> getModel(Player player) {
        getScoreboard().setDisplayName("§6§lHUNGER GAMES");
        perPlayer.clear();

        Profile profile = Profile.getProfile(player);

        perPlayer.add("");

        perPlayer.add("§eHG Mix:");
        perPlayer.add(" §fWins: §b" +
                String.format("%,d", profile.getData(DataType.HG_TOTAL_WINS).getAsInt()));
        perPlayer.add(" §fKills: §b" +
                String.format("%,d", profile.getData(DataType.HG_TOTAL_KILLS).getAsInt()));

        perPlayer.add("");

        perPlayer.add("§eChampions League:");
        perPlayer.add(" §fWins: §b" +
                String.format("%,d", profile.getData(DataType.CHAMPIONS_LEAGUE_WINS).getAsInt()));
        perPlayer.add(" §fKills: §b" +
                String.format("%,d", profile.getData(DataType.CHAMPIONS_LEAGUE_KILLS).getAsInt()));

        perPlayer.add("");

        perPlayer.add("§fCoins: §6" +
                String.format("%,d", profile.getData(DataType.COINS).getAsInt()));
        perPlayer.add("§fJogadores: §a" +
                String.format("%,d", Lobby.getControl().getController(ProxiedServerController.class)
                        .getOnlineCount(ServerType.PROXY)));

        perPlayer.add("");

        perPlayer.add("§ewww.lostmc.com.br");

        return perPlayer;
    }
}
