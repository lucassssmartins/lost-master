package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

public class PacketSetPlayersCapacity extends Packet {

    public PacketSetPlayersCapacity(String serverId, int capacity) {
        super(new JsonBuilder().addProperty("serverId", serverId).addProperty("capacity", capacity)
                .build());
    }

    public String getServerId() {
        return this.json.get("serverId").getAsString();
    }

    public int getCapacity() {
        return this.json.get("capacity").getAsInt();
    }
}
