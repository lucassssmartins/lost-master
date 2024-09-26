package com.lostmc.core.storage;

import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.storage.account.AccountStorage;
import com.lostmc.core.storage.punishment.PunishmentStorage;
import lombok.Getter;

public class StorageCommon {

    public void initializeParams(MySQLBackend backend) {
        accountStorage = new AccountStorage(backend);
        punishmentStorage = new PunishmentStorage(backend);
    }

    public void initialize() {
        try {
            accountStorage.initialize();
            punishmentStorage.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    private AccountStorage accountStorage;

    @Getter
    private PunishmentStorage punishmentStorage;
}
