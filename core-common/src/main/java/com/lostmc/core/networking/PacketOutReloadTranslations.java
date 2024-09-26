package com.lostmc.core.networking;

import com.lostmc.core.utils.JsonBuilder;

public class PacketOutReloadTranslations extends Packet {

    public PacketOutReloadTranslations() {
        super(new JsonBuilder().build());
    }
}
