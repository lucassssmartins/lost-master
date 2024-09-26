package com.lostmc.core.server;

import lombok.Getter;

@Getter
public enum ServerType {

    // AUTHENTICATION
    AUTH,

    // LOBBY
    MAIN_LOBBY(true),
    HG_LOBBY(true),

    // PVP
    PVP,

    // HG
    HUNGERGAMES,
    HG_EVENT,

    // OTHERS
    PROXY,
    TEST,
    UNDEFINED;

    private boolean hub;

    ServerType(boolean hub) {
        this.hub = hub;
    }

    ServerType() {
        this(false);
    }
}
