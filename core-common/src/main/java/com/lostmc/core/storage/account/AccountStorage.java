package com.lostmc.core.storage.account;

import com.google.gson.reflect.TypeToken;
import com.lostmc.core.Commons;
import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.storage.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class AccountStorage extends Storage {

    public AccountStorage(MySQLBackend backend) {
        super(backend);
    }

    @Override
    public String getTableName() {
        return "profile_holder";
    }

    @Override
    public void initialize() throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection();
             Statement createStmt = con.createStatement()) {
             createStmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` ("
                    + "`uuid` CHAR(36), "
                    + "`json` LONGTEXT NOT NULL);");
        }
    }

    public Profile createProfile(UUID id, String name) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement createStmt = con.prepareStatement("INSERT INTO `" +
                    getTableName() + "` (`uuid`, `json`) VALUES (?, ?)")) {

                Profile profile = new Profile(id, name);

                createStmt.setString(1, profile.getUniqueId().toString());
                createStmt.setString(2, Commons.getGson().toJson(profile));

                createStmt.execute();

                return profile;
            }
        }
    }

    public Profile getProfile(UUID id) throws Exception {
        try (Connection con = getBackend().getDataSource().getConnection()) {
            try (PreparedStatement getStmt = con.prepareStatement("SELECT * FROM `" +
                    getTableName() + "` WHERE `uuid`=?")) {

                getStmt.setString(1, id.toString());

                try (ResultSet resultSet = getStmt.executeQuery()) {
                    if (resultSet.next()) {
                        return Commons.getGson().fromJson(resultSet.getString(2), Profile.class);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public void saveProfile(Profile profile) {
        Commons.getPlatform().runAsync(() -> {
            try (Connection con = getBackend().getDataSource().getConnection()) {
                try (PreparedStatement saveStmt = con.prepareStatement("UPDATE `" +
                        getTableName() + "` SET `json`=? WHERE `uuid`=?")) {

                    saveStmt.setString(1, Commons.getGson().toJson(profile));
                    saveStmt.setString(2, profile.getUniqueId().toString());

                    saveStmt.execute();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
