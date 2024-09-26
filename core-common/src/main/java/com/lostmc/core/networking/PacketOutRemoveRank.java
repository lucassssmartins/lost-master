package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketOutRemoveRank extends Packet {

    public PacketOutRemoveRank(UUID playerUniqueId, int index) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("index", index).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public int getIndex() {
        return this.json.get("index").getAsInt();
    }
}
