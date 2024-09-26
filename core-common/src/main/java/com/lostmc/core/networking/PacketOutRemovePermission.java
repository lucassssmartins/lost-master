package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketOutRemovePermission extends Packet {

    public PacketOutRemovePermission(UUID uniqueId, String permission) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("permission", permission).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(this.json.get("uniqueId").getAsString());
    }

    public String getPermission() {
        return this.json.get("permission").getAsString();
    }
}
