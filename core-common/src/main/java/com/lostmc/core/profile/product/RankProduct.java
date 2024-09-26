package com.lostmc.core.profile.product;

import com.lostmc.core.profile.rank.Rank;

import java.util.UUID;

public class RankProduct extends Product<Rank> {

    public RankProduct(Rank object, String name, UUID senderUniqueId, String senderName,
                       boolean paid, long expirationTime) {
        super(object, name, senderUniqueId, senderName, paid, expirationTime);
    }
}
