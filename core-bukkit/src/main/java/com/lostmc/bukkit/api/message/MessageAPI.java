package com.lostmc.bukkit.api.message;

import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageAPI {

    public static void sendAlert(String alertMessage) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            if (!ps.hasPermission("core.moderate.alerts"))
                continue;
            if (!Profile.getProfile(ps).getData(DataType.AC_ALERTS).getAsBoolean())
                continue;
            ps.sendMessage("§8§o[" + alertMessage + "]");
        }
    }
}
