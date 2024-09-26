package com.lostmc.core.profile.division;

import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DivisionHandler {

    @Getter
    private static final DivisionHandler instance = new DivisionHandler();
    private final Map<String, Division> divisions = new LinkedHashMap<>();
    @Setter
    @Getter
    private EloCalculator eloCalculator = new EloCalculator();

    public DivisionHandler() {
        // BRONZE
        newDivision(ChatColor.RED, "Bronze I", "Bronze", "", 0, 649);
        newDivision(ChatColor.RED, "Bronze II", "Bronze", "", 650, 724);
        newDivision(ChatColor.RED, "Bronze III", "Bronze", "", 725, 799);
        newDivision(ChatColor.RED, "Bronze IV", "Bronze", "", 800, 874);

        // SILVER
        newDivision(ChatColor.GRAY, "Silver I", "Silver", "", 875, 939);
        newDivision(ChatColor.GRAY, "Silver II", "Silver", "", 940, 999);
        newDivision(ChatColor.GRAY, "Silver III", "Silver", "", 1000, 1059);
        newDivision(ChatColor.GRAY, "Silver IV", "Silver", "", 1060, 1119);

        // GOLD
        newDivision(ChatColor.GOLD, "Gold I", "Gold", "", 1120, 1179);
        newDivision(ChatColor.GOLD, "Gold II", "Gold", "", 1180, 1239);
        newDivision(ChatColor.GOLD, "Gold III", "Gold", "", 1240, 1299);
        newDivision(ChatColor.GOLD, "Gold IV", "Gold", "", 1300, 1359);

        // DIAMOND
        newDivision(ChatColor.AQUA, "Diamond I", "Diamond", "", 1360, 1419);
        newDivision(ChatColor.AQUA, "Diamond II", "Diamond", "", 1420, 1489);
        newDivision(ChatColor.AQUA, "Diamond III", "Diamond", "", 1490, 1539);
        newDivision(ChatColor.AQUA, "Diamond IV", "Diamond", "", 1540, 1599);

        // LEGENDARY
        newDivision(ChatColor.DARK_PURPLE, "Legendary", "Legendary", "", 1600, Integer.MAX_VALUE);
    }

    private void newDivision(ChatColor color, String name, String modelName, String symbol,
                             int minElo, int maxElo) {
        this.divisions.put(name, new Division(color, name, modelName, symbol, minElo, maxElo));
    }

    public Division getDivisionByElo(int elo) {
        Division result = null;
        for (Division division : this.divisions.values()) {
            if (elo >= division.getMinElo()) {
                result = division;
            }
        }
        return result;
    }
}
