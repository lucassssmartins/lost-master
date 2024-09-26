package com.lostmc.core.profile.product;

import java.util.UUID;

public class PermissionProduct extends Product<String> {

    public PermissionProduct(String object, String name, UUID senderUniqueId, String senderName,
                             boolean paid, long expirationTime) {
        super(object, name, senderUniqueId, senderName, paid, expirationTime);
    }
}
