package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketCommand extends Packet {

    public PacketCommand(UUID playerUniqueId, String commandLine) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("commandLine", commandLine).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public String getCommandLine() {
        return this.json.get("commandLine").getAsString();
    }
}
