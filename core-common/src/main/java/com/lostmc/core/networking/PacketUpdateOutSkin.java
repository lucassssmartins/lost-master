package com.lostmc.core.networking;

import com.google.gson.JsonObject;
import com.lostmc.core.Commons;
import com.lostmc.core.property.IProperty;
import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketUpdateOutSkin extends Packet {

    public PacketUpdateOutSkin(UUID uniqueId, IProperty property) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString()).addProperty("iproperty",
                Commons.getGson().toJson(property)).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(json.get("uniqueId").getAsString());
    }

    public IProperty getProperty() {
        return Commons.getGson().fromJson(json.get("iproperty").getAsString(), IProperty.class);
    }
}
