package com.lostmc.core.networking;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.utils.JsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketUpdateMultipleDatas extends Packet {

    private static final Type dataTypeToken = new TypeToken<ArrayList<DataType>>(){}.getType();
    private static final Type elementTypeToken = new TypeToken<ArrayList<JsonElement>>(){}.getType();

    public PacketUpdateMultipleDatas(UUID uniqueId, DataType[] dataTypes, JsonElement[] jsonElements) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("dataTypes", Commons.getGson().toJson(createList(dataTypes)))
                .addProperty("jsonElements", Commons.getGson().toJson(createJsonElementList(jsonElements))).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(json.get("uniqueId").getAsString());
    }

    public List<DataType> getDataTypes() {
        return Commons.getGson().fromJson(json.get("dataTypes").getAsString(), dataTypeToken);
    }

    public List<JsonElement> getJsonElements() {
        return Commons.getGson().fromJson(json.get("jsonElements").getAsString(), elementTypeToken);
    }

    private static <T> ArrayList<T> createList(T... ts) {
        ArrayList<T> tList = new ArrayList<>();
        for (T t : ts)
            tList.add(t);
        return tList;
    }

    private static ArrayList<String> createJsonElementList(JsonElement... elements) {
        ArrayList<String> stringList = new ArrayList<>();
        for (JsonElement element : elements)
            stringList.add(Commons.getGson().toJson(element));
        return stringList;
    }
}
