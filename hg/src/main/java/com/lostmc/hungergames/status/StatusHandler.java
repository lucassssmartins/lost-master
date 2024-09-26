package com.lostmc.hungergames.status;

import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.division.DivisionHandler;
import com.lostmc.core.profile.division.EloCalculator;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import org.bukkit.entity.Player;

public class StatusHandler {

    public static void addDeath(Player p) {
        Profile profile = Profile.getProfile(p);
        profile.setData(DataType.HG_TOTAL_DEATHS, profile.getData(DataType.HG_TOTAL_DEATHS).getAsInt() + 1);
        if (((HungerGames) HungerGames.getInstance()).getGameMode().isDoubleKit())
            profile.setData(DataType.HG_DOUBLEKIT_DEATHS, profile.getData(DataType.HG_DOUBLEKIT_DEATHS).getAsInt() + 1);
        else
            profile.setData(DataType.HG_SINGLEKIT_DEATHS, profile.getData(DataType.HG_SINGLEKIT_DEATHS).getAsInt() + 1);
        int eloLost = 20;
        int ELO = profile.getData(DataType.HG_ELO).getAsInt();
        if (ELO > 0) {
            if (ELO <= eloLost)
                eloLost = ELO;
            profile.setData(DataType.HG_ELO, ELO - eloLost);
            p.sendMessage("§c-" + eloLost + " ELO");
        }
        profile.save();
    }

    public static void addKill(Player p, Player kill) {
        HungerGamer gamer = HungerGamer.getGamer(p);
        gamer.setMatchKills(gamer.getMatchKills() + 1);
        Profile profile = Profile.getProfile(p);
        profile.setData(DataType.HG_TOTAL_KILLS, profile.getData(DataType.HG_TOTAL_KILLS).getAsInt() + 1);
        if (((HungerGames) HungerGames.getInstance()).getGameMode().isDoubleKit())
            profile.setData(DataType.HG_DOUBLEKIT_KILLS, profile.getData(DataType.HG_DOUBLEKIT_KILLS).getAsInt() + 1);
        else
            profile.setData(DataType.HG_SINGLEKIT_KILLS, profile.getData(DataType.HG_SINGLEKIT_KILLS).getAsInt() + 1);
        int coins = 40;
        profile.setData(DataType.COINS, profile.getData(DataType.COINS).getAsInt() + coins);
        p.sendMessage("§6+" + coins + " coins");
        int winnerElo = profile.getData(DataType.HG_ELO).getAsInt();
        int loserElo = Profile.getProfile(kill).getData(DataType.HG_ELO).getAsInt();
        EloCalculator calculator = DivisionHandler.getInstance().getEloCalculator();
        EloCalculator.Result result = calculator.calculate(winnerElo, loserElo);
        int eloGain = result.getWinnerGain();
        profile.setData(DataType.HG_ELO, (winnerElo + eloGain));
        p.sendMessage("§b+" + eloGain + " ELO");
        profile.save();
    }

    public static void addAssist(Player p) {
        Profile profile = Profile.getProfile(p);
        if (((HungerGames) HungerGames.getInstance()).getGameMode().isDoubleKit())
            profile.setData(DataType.HG_DOUBLEKIT_ASSISTS, profile.getData(DataType.HG_DOUBLEKIT_ASSISTS).getAsInt() + 1);
         else
            profile.setData(DataType.HG_SINGLEKIT_ASSISTS, profile.getData(DataType.HG_SINGLEKIT_ASSISTS).getAsInt() + 1);
        profile.save();
        p.sendMessage("§bVocê pegou uma assistência!");
    }
}
