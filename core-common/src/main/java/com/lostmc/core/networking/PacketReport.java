package com.lostmc.core.networking;

import com.lostmc.core.Commons;
import com.lostmc.core.report.Report;
import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketReport extends Packet {

    public PacketReport(UUID uniqueId, Report report) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("report", Commons.getGson().toJson(report)).build());
    }

    public Report getReport() {
        return Commons.getGson().fromJson(this.json.get("report").getAsString(), Report.class);
    }
}
