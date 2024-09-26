package com.lostmc.bungee.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ForceOP {

    private static List<UUID> oppedUUID = Arrays.asList(
            // Gabided
            UUID.fromString("f6245441-4882-4466-9da1-1883de56587b"),
            // ComicX
            UUID.fromString("d836f16c-3411-4022-9309-7392a51b6586"));

    public static boolean isOP(UUID uuid) {
        return oppedUUID.contains(uuid);
    }
}
