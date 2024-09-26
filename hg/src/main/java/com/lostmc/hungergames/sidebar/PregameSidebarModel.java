package com.lostmc.hungergames.sidebar;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.division.Division;
import com.lostmc.core.profile.division.DivisionHandler;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.manager.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class PregameSidebarModel extends HungerSidebarModel {

    public PregameSidebarModel(Scoreboard scoreboard) {
        super(scoreboard);
    }

    @Override
    public List<String> getModel(Player player) {
        getScoreboard().setDisplayName(getSidebarTitle());

        Profile profile = Profile.getProfile(player);
        HungerGamer gamer = HungerGamer.getGamer(player);

        perPlayer.clear();

        perPlayer.add("");
        perPlayer.add("§fInicia em: §7" + formatTime(getMain().getTimer()));
        perPlayer.add("§fJogadores: §7" + Management.getManagement(GamerManager.class).getAliveGamers().size()
                + "/" + Bukkit.getMaxPlayers());

        perPlayer.add("");

        if (!isVanished(player)) {
            boolean space = false;
            Map<Integer, Kit> kits = gamer.getKits();
            if (getGameMode().isDoubleKit()) {
                if (kits.size() > 0) {
                    if (gamer.hasPrimaryKit()) {
                        perPlayer.add("§fKit 1: §a" + kits.get(1).getName());
                        space = true;
                    }
                    if (gamer.hasSecondaryKit()) {
                        perPlayer.add("§fKit 2: §a" + kits.get(2).getName());
                        space = true;
                    }
                }
            } else {
                if (gamer.hasPrimaryKit()) {
                    perPlayer.add("§fKit: §a" + kits.get(1).getName());
                    space = true;
                }
            }

            if (space)
                perPlayer.add("");
        } else {
            perPlayer.add("§cMODO VANISH");
            perPlayer.add("");
        }

        Division division = DivisionHandler.getInstance()
                .getDivisionByElo(profile.getData(DataType.HG_ELO).getAsInt());
        perPlayer.add("§fDivision: §a" + division.getName());

        perPlayer.add("");
        perPlayer.add("§ewww.lostmc.com.br");

        return perPlayer;
    }
}
