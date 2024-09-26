package com.lostmc.core.report;

import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketReport;
import com.lostmc.core.networking.PacketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Report {

    public static Report fromUniqueId(UUID uniqueId) {
        return Commons.getReportMap().get(uniqueId);
    }

    public static Report fromName(String name) {
        return Commons.getReportMap().values().stream()
                .filter(report -> report.getPlayerName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private final UUID playerUniqueId;
    private final String playerName;
    private final String playerServer;
    private final List<ReportInfo> reports = new ArrayList<>();
    @Setter
    private ReportInfo lastReport;
    private ReportLevel level = ReportLevel.values()[0];

    public void addReport(ReportInfo reportInfo) {
        setLastReport(reportInfo);
        getReports().add(reportInfo);
        checkLevel();

        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.publish(PacketType.REPORT_CREATE.toString(),
                    Commons.getGson().toJson(new PacketReport(getPlayerUniqueId(), this)));
        }
    }

    public void remove() {
        Commons.getReportMap().remove(playerUniqueId);
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.publish(PacketType.REPORT_REMOVE.toString(),
                    Commons.getGson().toJson(new PacketReport(getPlayerUniqueId(), this)));
        }
    }

    public void checkLevel() {
        if (level == ReportLevel.values()[ReportLevel.values().length - 1])
            return;

        ReportLevel nextLevel = ReportLevel.values()[level.ordinal() + 1];
        if (reports.size() > nextLevel.getRequired())
            level = nextLevel;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > getLastReport().getExpires();
    }

    @Getter
    @AllArgsConstructor
    public enum ReportLevel {

        I(0, "§a"), II(5, "§e"), III(15, "§6"), IV(25, "§c"), V(35, "§4");

        private final int required;
        private final String color;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ReportInfo {

        private final UUID reporterUniqueId;
        private final String reporterName;
        private final String reason;
        private final long time = System.currentTimeMillis();
        private final long expires = time + 600000L;
    }
}
