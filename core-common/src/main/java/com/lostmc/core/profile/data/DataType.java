package com.lostmc.core.profile.data;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public enum DataType {

    // PREFERENCES
    PLAY_SOUNDS(false, Boolean.class, boolean.class),
    CHAT(true, Boolean.class, boolean.class),
    PRIVATE_MESSAGES(true, Boolean.class, boolean.class),
    PARTY_INVITES(true, Boolean.class, boolean.class),
    FRIEND_INVITES(true, Boolean.class, boolean.class),
    AC_ALERTS(true, Boolean.class, boolean.class),
    STAFF_CHAT_MESSAGES(true, Boolean.class, boolean.class),
    STAFF_CHAT_MODE(false, Boolean.class, boolean.class),
    CLAN_INVITES(true, Boolean.class, boolean.class),
    CLAN_CHAT_MESSAGES(true, Boolean.class, boolean.class),
    CLAN_CHAT_MODE(false, Boolean.class, boolean.class),
    PUBLIC_STATISTICS(true, Boolean.class, boolean.class),
    EXTRA_DETAILS(true, Boolean.class, boolean.class),
    AUTO_JOIN_VANISHED(true, Boolean.class, boolean.class),
    HUB_PLAYERS(true, Boolean.class, boolean.class),
    HUB_FLIGHT_MODE(false, Boolean.class, boolean.class),

    // AUTH
    ACCOUNT_PASSWORD("", String.class),

    // SETTINGS
    NICKNAME("", String.class),
    BUILD_MODE(false, Boolean.class, boolean.class),
    REDEEMED_VIP_TEST(false, Boolean.class, boolean.class),

    // BALANCE
    COINS(0, Integer.class, int.class),
    SUCCESSFUL_REPORTS(0, Integer.class, int.class),

    // CONNECTION
    LAST_IP_ADDRESS("", String.class),

    // LOGIN
    FIRST_LOGGED_IN(0L, Long.class, long.class),
    LAST_LOGGED_IN(0L, Long.class, long.class),

    //SOCIAL MEDIA
    TWITTER_LINK("", String.class),
    YOUTUBE_LINK("", String.class),
    INSTAGRAM_LINK("", String.class),
    TWITCH_LINK("", String.class),
    TIKTOK_LINK("", String.class),
    DISCORD_LINK("", String.class),

    // 1v1
    FIGHT_1V1_ELO(1000, Integer.class, int.class),
    FIGHT_1V1_KILLS(0, Integer.class, int.class),
    FIGHT_1V1_DEATHS(0, Integer.class, int.class),
    FIGHT_1V1_KS(0, Integer.class, int.class),
    FIGHT_1V1_GREATER_KS(0, Integer.class, int.class),

    // GLADIATOR
    FIGHT_GLAD_ELO(1000, Integer.class, int.class),
    FIGHT_GLAD_KILLS(0, Integer.class, int.class),
    FIGHT_GLAD_DEATHS(0, Integer.class, int.class),
    FIGHT_GLAD_KS(0, Integer.class, int.class),
    FIGHT_GLAD_GREATER_KS(0, Integer.class, int.class),

    // PVP
    PVP_GLOBAL_KILLS(0, Integer.class, int.class),
    PVP_GLOBAL_DEATHS(0, Integer.class, int.class),
    PVP_GLOBAL_KS(0, Integer.class, int.class),
    PVP_GLOBAL_GREATER_KS(0, Integer.class, int.class),

    PVP_LAVA_EASY_COMPLETIONS(0, Integer.class, int.class),
    PVP_LAVA_MEDIUM_COMPLETIONS(0, Integer.class, int.class),
    PVP_LAVA_HARD_COMPLETIONS(0, Integer.class, int.class),
    PVP_LAVA_EXTREME_COMPLETIONS(0, Integer.class, int.class),

    PVP_MLG_I_HITS(0, Integer.class, int.class),
    PVP_MLG_II_HITS(0, Integer.class, int.class),
    PVP_MLG_III_HITS(0, Integer.class, int.class),
    PVP_MLG_IV_HITS(0, Integer.class, int.class),

    PVP_CHALLENGE_EASY_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_MEDIUM_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_HARD_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_EXTREME_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_VARIED_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_WITHER_TIMER(0, Integer.class, int.class),
    PVP_CHALLENGE_DROPS_TIMER(0, Integer.class, int.class),

    PVP_FAVORITE_KITS(null, ArrayList.class),

    // HG
    HG_ELO(1000, Integer.class, int.class),
    HG_TOTAL_WINS(0, Integer.class, int.class),
    HG_SINGLEKIT_WINS(0, Integer.class, int.class),
    HG_DOULEKIT_WINS(0, Integer.class, int.class),

    HG_TOTAL_KILLS(0, Integer.class, int.class),
    HG_SINGLEKIT_KILLS(0, Integer.class, int.class),
    HG_DOUBLEKIT_KILLS(0, Integer.class, int.class),

    HG_TOTAL_DEATHS(0, Integer.class, int.class),
    HG_SINGLEKIT_DEATHS(0, Integer.class, int.class),
    HG_DOUBLEKIT_DEATHS(0, Integer.class, int.class),

    HG_TOTAL_ASSISTS(0, Integer.class, int.class),
    HG_SINGLEKIT_ASSISTS(0, Integer.class, int.class),
    HG_DOUBLEKIT_ASSISTS(0, Integer.class, int.class),

    HG_FAVORITE_KITS(null, ArrayList.class),

    // HG CHAMPIONS LEAGUE
    CHAMPIONS_LEAGUE_ELO(1000, Integer.class, int.class),
    CHAMPIONS_LEAGUE_WINS(0, Integer.class, int.class),
    CHAMPIONS_LEAGUE_KILLS(0, Integer.class, int.class),
    CHAMPIONS_LEAGUE_DEATHS(0, Integer.class, int.class);

    private Object value;
    private Class<?> wrapperClass, primitiveClass;

    DataType(Object content, Class<?> type, Class<?> primitiveClass){
        this.value = content;
        this.wrapperClass = type;
        this.primitiveClass = primitiveClass;
    }

    DataType(Object value, Class<?> wrapperClass) {
        this(value, wrapperClass, null);
    }

    public boolean hasPrimitiveClass() {
        return this.primitiveClass != null;
    }

    public Object generateNewValue() {
        if (this.value != null) {
            try {
                return this.wrapperClass.getConstructor(hasPrimitiveClass() ? this.primitiveClass
                        : this.wrapperClass).newInstance(this.value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                return this.wrapperClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
