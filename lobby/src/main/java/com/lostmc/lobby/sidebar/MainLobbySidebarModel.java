package com.lostmc.lobby.sidebar;

import com.google.common.collect.Lists;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardModel;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.entity.Player;
import java.util.List;

public class MainLobbySidebarModel extends ScoreboardModel {

    private List<String> perPlayer = Lists.newLinkedList();

    public MainLobbySidebarModel(Scoreboard scoreboard) {
        super(scoreboard);
    }

    @Override
    public List<String> getModel(Player player) {
        getScoreboard().setDisplayName("§6§lLOST");
        perPlayer.clear();

        Profile profile = Profile.getProfile(player);

        perPlayer.add("");

        perPlayer.add("§fRank: " + Tag.fromName(profile.getRank().toString()).getColouredName(false));

        perPlayer.add("");

        perPlayer.add("§fCoins: §b"
                + String.format("%,d", profile.getData(DataType.COINS).getAsInt()));
        perPlayer.add("Conquistas: §b" + 0);

        perPlayer.add("");

        perPlayer.add("§fLobby: §7#" + Commons.getProxyHandler().getLocal().getId().replaceAll("[a-zA-Z0-]", ""));
        perPlayer.add("§fJogadores: §a" + Lobby.getControl().getController(ProxiedServerController.class)
                .getOnlineCount(ServerType.PROXY));

        perPlayer.add("");

        perPlayer.add("§ewww.lostmc.com.br");

        return perPlayer;
    }
}
