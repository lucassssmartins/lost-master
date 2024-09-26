package com.lostmc.core.storage.punishment;

import com.google.gson.JsonObject;
import com.lostmc.core.Commons;
import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.punishment.BanPunishment;
import com.lostmc.core.punishment.IPBanPunishment;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.SilentPunishment;
import com.lostmc.core.storage.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentStorage extends Storage {

    public PunishmentStorage(MySQLBackend backend) {
        super(backend);
    }

    @Override
    public String getTableName() {
        return "punishment_holder";
    }

    @Override
    public void initialize() throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection();
             Statement createStmt = con.createStatement()) {
            createStmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` ("
                    + "`id` CHAR(36), "
                    + "`json` LONGTEXT NOT NULL);");
        }
    }

    public void recordPunishment(Punishment punishment) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement createStmt = con.prepareStatement("INSERT INTO `" +
                    getTableName() + "` (`id`, `json`) VALUES (?, ?)")) {

                createStmt.setString(1, punishment.getId());
                createStmt.setString(2, Commons.getGson().toJson(punishment));

                createStmt.execute();
            }
        }
    }

    public void pardonPunishment(String id) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM `" +
                    getTableName() + "` WHERE `id` = ?")) {
                deleteStmt.setString(1, id);
                deleteStmt.execute();
            }
        }
    }

    public IPBanPunishment getIPBanPunishment(String ipAddress) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement getStmt = con.prepareStatement("SELECT * FROM `" +
                    getTableName() + "` WHERE JSON_EXTRACT(`json`, '$.ipAddress') = ?")) {

                getStmt.setString(1, ipAddress);

                try (ResultSet resultSet = getStmt.executeQuery()) {
                    if (resultSet.next()) {
                        return Commons.getGson().fromJson(resultSet.getString(2),
                                IPBanPunishment.class);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public Punishment getPunishment(String id) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement getStmt = con.prepareStatement("SELECT * FROM `" +
                    getTableName() + "` WHERE `id`=?")) {

                getStmt.setString(1, id);

                try (ResultSet resultSet = getStmt.executeQuery()) {
                    if (resultSet.next()) {
                        JsonObject jsonObject = Commons.getGson().fromJson(resultSet.getString(2),
                                JsonObject.class);
                        switch (Punishment.Type.valueOf(jsonObject.get("type").getAsString())) {
                            case BAN:
                                return Commons.getGson().fromJson(jsonObject.toString(), BanPunishment.class);
                            case IP_BAN:
                                return Commons.getGson().fromJson(jsonObject.toString(), IPBanPunishment.class);
                            case SILENT:
                                return Commons.getGson().fromJson(jsonObject.toString(), SilentPunishment.class);
                            default:
                                return Commons.getGson().fromJson(jsonObject.toString(), Punishment.class);
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public List<Punishment> getActorPunishments(UUID actorUniqueId, Punishment.Type optionalFilter) throws Exception {
        List<Punishment> punishmentList = new ArrayList<>();

        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement getStmt = con.prepareStatement("SELECT * FROM `" +
                    getTableName() + "` WHERE JSON_EXTRACT(`json`, '$.actorData.uniqueId') = ?")) {

                getStmt.setString(1, actorUniqueId.toString());

                try (ResultSet resultSet = getStmt.executeQuery()) {
                    while (resultSet.next()) {
                        JsonObject jsonObject = Commons.getGson().fromJson(resultSet.getString(2),
                                JsonObject.class);
                        Punishment.Type type = Punishment.Type.valueOf(jsonObject.get("type").getAsString());
                        if (optionalFilter != null && type != optionalFilter)
                            continue;
                        switch (type) {
                            case BAN:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        BanPunishment.class));
                                break;
                            case IP_BAN:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        IPBanPunishment.class));
                                break;
                            case SILENT:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        SilentPunishment.class));
                                break;
                            default:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        Punishment.class));
                                break;
                        }
                    }
                }
            }
        }

        return punishmentList;
    }

    public List<Punishment> getPlayerPunishments(UUID playerUniqueId, Punishment.Type optionalFilter) throws Exception {
        List<Punishment> punishmentList = new ArrayList<>();

        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement getStmt = con.prepareStatement("SELECT * FROM `" +
                    getTableName() + "` WHERE JSON_EXTRACT(`json`, '$.playerData.uniqueId') = ?")) {

                getStmt.setString(1, playerUniqueId.toString());

                try (ResultSet resultSet = getStmt.executeQuery()) {
                    while (resultSet.next()) {
                        JsonObject jsonObject = Commons.getGson().fromJson(resultSet.getString(2),
                                JsonObject.class);
                        Punishment.Type type = Punishment.Type.valueOf(jsonObject.get("type").getAsString());
                        if (optionalFilter != null && type != optionalFilter)
                            continue;
                        switch (type) {
                            case BAN:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        BanPunishment.class));
                                break;
                            case IP_BAN:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        IPBanPunishment.class));
                                break;
                            case SILENT:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        SilentPunishment.class));
                                break;
                            default:
                                punishmentList.add(Commons.getGson().fromJson(jsonObject.toString(),
                                        Punishment.class));
                                break;
                        }
                    }
                }
            }
        }

        return punishmentList;
    }
}
