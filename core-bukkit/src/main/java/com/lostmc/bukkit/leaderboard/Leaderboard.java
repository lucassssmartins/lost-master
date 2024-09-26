package com.lostmc.bukkit.leaderboard;

import com.google.gson.reflect.TypeToken;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.storage.account.AccountStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Leaderboard {

    private final DataType dataType;
    private final int size;
    private Map<Integer, Profile> topPlayers = new HashMap<>();
    private boolean loaded = false;
    private long lastUpdate = 0L;

    public void update() {
        lastUpdate = System.currentTimeMillis();
        try {
            try (Connection connection = Commons.getMysqlBackend().getDataSource().getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "SELECT * FROM `profile_holder` ORDER BY CAST(JSON_UNQUOTE(JSON_EXTRACT(`json`, '$.datas." + dataType.toString()
                                + "')) AS INT) DESC LIMIT " + size)) {
                    try (ResultSet resultSet = stmt.executeQuery()) {
                        int i = 1;
                        while (resultSet.next()) {
                            this.topPlayers.put(i, Commons.getGson().fromJson(resultSet.getString(2),
                                    Profile.class));
                            ++i;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!loaded)
            loaded = true;
    }

    public static void main(String[] args) {
        System.out.print("");
    }
}
