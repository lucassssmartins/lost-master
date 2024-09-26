package com.lostmc.core.networking;

import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.utils.JsonBuilder;
import com.lostmc.core.utils.JsonUtils;
import com.lostmc.core.Commons;

import java.util.UUID;

public class PacketOutAddRank extends Packet {

    public PacketOutAddRank(UUID playerUniqueId, RankProduct rankProduct) {
        super(new JsonBuilder().addProperty("playerUniqueId", playerUniqueId.toString())
                .addProperty("rankProduct", Commons.getGson().toJson(rankProduct)).build());
    }

    public UUID getPlayerUniqueId() {
        return UUID.fromString(this.json.get("playerUniqueId").getAsString());
    }

    public RankProduct getRankProduct() {
        return JsonUtils.jsonToObject(this.json.get("rankProduct").getAsString(), RankProduct.class);
    }
}
