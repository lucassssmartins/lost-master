package com.lostmc.core.profile.rank;

import com.lostmc.core.profile.tag.Tag;

public enum Rank {

    ADMIN,

    MANAGER,

    MODPLUS,
    MOD,
    TRIAL,

    DESIGNER,
    BUILDER,

    YTPLUS,
    YT,
    STREAMER,
    PARTNER,


    BETA,
    LOST,
    PRO,
    MVP,
    VIP,

    DEFAULT;

    public static Rank fromTag(Tag tag) {
        try {
            return Rank.valueOf(tag.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
