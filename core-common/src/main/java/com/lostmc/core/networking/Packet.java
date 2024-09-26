package com.lostmc.core.networking;

import com.google.gson.JsonObject;
import com.lostmc.core.Commons;

public abstract class Packet {

    protected JsonObject json;

    public Packet(JsonObject json) {
        this.json = json;
    }

    public String toJson() {
        return Commons.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "json=" + json +
                '}';
    }
}
