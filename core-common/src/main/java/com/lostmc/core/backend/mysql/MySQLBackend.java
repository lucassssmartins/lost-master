package com.lostmc.core.backend.mysql;

import com.lostmc.core.Commons;
import com.lostmc.core.backend.Backend;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

public class MySQLBackend implements Backend {

    private HikariConfig config;
    @Getter
    private HikariDataSource dataSource;

    public MySQLBackend(String jdbcURL, HikariConfig config, String host, int port,
                        String database, boolean useSSL) {
        this.config = setParams(config, useSSL);
        this.config.setPoolName(Commons.getPlatform().getName());
        this.config.setJdbcUrl("jdbc:" + buildJDBCUrl(host, port, database));
    }

    private String buildJDBCUrl(String host, int port, String database) {
        return "mysql" + "://" + host + ':' + port + '/' + database;
    }

    private HikariConfig setParams(HikariConfig config, boolean useSSL) {
        config.addDataSourceProperty("useSSL", useSSL);
        config.addDataSourceProperty("requireSSL", useSSL);
        config.addDataSourceProperty("paranoid", true);
        addPerformanceProperties(config);
        return config;
    }

    private static void addPerformanceProperties(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
    }

    @Override
    public void connect() throws Throwable {
        dataSource = new HikariDataSource(this.config);
    }

    @Override
    public void disconnect() throws Throwable {
        dataSource.close();
    }

    @Override
    public boolean isConnected() {
        return !dataSource.isClosed();
    }
}
