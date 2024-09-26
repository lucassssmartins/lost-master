package com.lostmc.hungergames.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<String> getFormattedLore(String text) {

        List<String> lore = new ArrayList<>();
        String[] split = text.split(" ");
        text = "";

        for (int i = 0; i < split.length; ++i) {
            if (ChatColor.stripColor(text).length() > 25 || ChatColor.stripColor(text).endsWith(".")
                    || ChatColor.stripColor(text).endsWith("!")) {
                lore.add("§7" + text);
                if (text.endsWith(".") || text.endsWith("!")) {
                    lore.add("");
                }
                text = "";
            }
            String toAdd = split[i];
            if (toAdd.contains("\n")) {
                toAdd = toAdd.substring(0, toAdd.indexOf("\n"));
                split[i] = split[i].substring(toAdd.length() + 1);
                lore.add("§7" + text + ((text.length() == 0) ? "" : " ") + toAdd);
                text = "";
                --i;
            } else {
                text += ((text.length() == 0) ? "" : " ") + toAdd;
            }
        }
        lore.add("§7" + text);

        return lore;
    }
}
