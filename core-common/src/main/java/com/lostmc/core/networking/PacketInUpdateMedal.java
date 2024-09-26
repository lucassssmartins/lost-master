package com.lostmc.core.networking;

import com.google.gson.JsonObject;
import com.lostmc.core.profile.medal.Medal;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketInUpdateMedal extends Packet {

    public PacketInUpdateMedal(UUID playerUniqueId, Medal medal) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("medal", medal.toString()).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public Medal getMedal() {
        return Medal.valueOf(this.json.get("medal").getAsString());
    }
}
