package com.lostmc.core.networking;

import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.utils.JsonBuilder;
import com.lostmc.core.Commons;
import com.google.gson.JsonElement;

import java.util.UUID;

public class PacketUpdateSingleData extends Packet {

    public PacketUpdateSingleData(UUID uniqueId, DataType dataType, JsonElement jsonElement) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("dataType", dataType.toString()).addElement("jsonElement", jsonElement).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(this.json.get("uniqueId").getAsString());
    }

    public DataType getDataType() {
        return DataType.valueOf(this.json.get("dataType").getAsString());
    }

    public JsonElement getJsonElement() {
        return Commons.getGson().toJsonTree(this.json.get("jsonElement"));
    }
}
