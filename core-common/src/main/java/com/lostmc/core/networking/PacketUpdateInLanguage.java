package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;
import com.lostmc.core.Commons;

import java.util.Locale;
import java.util.UUID;

public class PacketUpdateInLanguage extends Packet {

    public PacketUpdateInLanguage(UUID playerUniqueId, Locale locale) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("locale", Commons.getGson().toJson(locale)).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public Locale getLocale() {
        return Commons.getGson().fromJson(this.json.get("locale").getAsString(), Locale.class);
    }
}
