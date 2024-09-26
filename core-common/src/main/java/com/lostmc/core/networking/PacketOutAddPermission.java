package com.lostmc.core.networking;

import com.lostmc.core.Commons;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.utils.JsonBuilder;

import java.util.UUID;

public class PacketOutAddPermission extends Packet {

    public PacketOutAddPermission(UUID uniqueId, PermissionProduct permission) {
        super(new JsonBuilder().addProperty("uniqueId", uniqueId.toString())
                .addProperty("permission", Commons.getGson().toJson(permission)).build());
    }

    public UUID getUniqueId() {
        return UUID.fromString(this.json.get("uniqueId").getAsString());
    }

    public PermissionProduct getPermission() {
        return Commons.getGson().fromJson(this.json.get("permission").getAsString(), PermissionProduct.class);
    }
}
