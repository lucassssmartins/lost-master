package com.lostmc.core.storage;

import com.lostmc.core.backend.mysql.MySQLBackend;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Storage {

    private final MySQLBackend backend;

    public abstract String getTableName();

    public abstract void initialize() throws Exception;
}
