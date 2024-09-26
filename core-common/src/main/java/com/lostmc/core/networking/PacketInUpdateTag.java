package com.lostmc.core.networking;

import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketInUpdateTag extends Packet {

    public PacketInUpdateTag(UUID playerUniqueId, Tag tag) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("tag", tag.toString()).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public Tag getTag() {
        return Tag.valueOf(this.json.get("tag").getAsString());
    }
}
