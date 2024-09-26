package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketInExpiredPermission extends Packet{

    public PacketInExpiredPermission(UUID uniqueId, int index) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("index", index).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(this.json.get("uniqueId").getAsString());
    }

    public int getIndex() {
        return this.json.get("index").getAsInt();
    }
}
