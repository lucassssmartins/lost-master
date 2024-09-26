package com.lostmc.lobby.sidebar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreboardProvider {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static Date date = new Date();

    public static String getDate() {
        return formatter.format(date);
    }
}
