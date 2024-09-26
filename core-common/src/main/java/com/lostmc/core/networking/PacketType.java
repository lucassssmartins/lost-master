package com.lostmc.core.networking;

import java.util.ArrayList;
import java.util.List;

public enum PacketType {

    SERVER_INFO_UPDATING,
    SERVER_INFO_STOPPING,
    SERVER_INFO_STARTING,
    SERVER_INFO_UPDATE,

    RELOAD_TRANSLATIONS,

    ADD_RANK,
    REMOVE_RANK,

    IN_COMMAND,
    OUT_COMMAND,

    SET_IN_PLAYERS_CAPACITY,
    SET_OUT_PLAYERS_CAPACITY,

    EXPIRED_RANK,

    UPDATE_OUT_SKIN,

    UPDATE_IN_SINGLE_DATA,
    UPDATE_OUT_SINGLE_DATA,

    UPDATE_IN_MULTIPLE_DATAS,

    ADD_OUT_PERMISSION,
    REMOVE_OUT_PERMISSIONS,
    EXPIRED_IN_PERMISSION,

    UPDATE_IN_CUSTOM_PERMISSIONS,

    UPDATE_IN_MEDAL,
    UPDATE_IN_TAG,

    UPDATE_IN_LANGUAGE,

    REPORT_CREATE,
    REPORT_REMOVE;

    public static String[] toChannelsArray() {
        List<String> list = new ArrayList<>();
        for (PacketType type : values())
            list.add(type.toString());
        return list.toArray(new String[] {});
    }
}
