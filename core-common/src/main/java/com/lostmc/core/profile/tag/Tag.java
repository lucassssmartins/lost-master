package com.lostmc.core.profile.tag;

import com.lostmc.core.profile.rank.Rank;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum Tag {

    ADMIN('4', "Admin", "§4§lADMIN§4 ", Category.NORMAL, "adm"),

    MANAGER('c', "Gerente", "§c§lGERENTE§c ", Category.NORMAL, "ger"),

    MODPLUS('5', "Mod+", "§5§lMOD+§5 ", Category.NORMAL, "moderador+", "moderadorplus"),
    MOD('5', "Mod", "§5§lMOD§5 ", Category.NORMAL, "moderador"),
    TRIAL('d', "Trial", "§d§lTRIAL§d ", Category.NORMAL, "tmod"),

    DESIGNER('2', "Designer", "§2§lDESIGNER§2 ", Category.NORMAL, "dzn"),
    BUILDER('e', "Builder", "§e§lBUILDER§e ", Category.NORMAL, "construtor"),

    YTPLUS('3', "YT+", "§3§lYT+§3 ", Category.NORMAL, "yt+", "youtuberplus"),

    YT('b', "YT", "§b§lYT§b ", Category.NORMAL, "youtuber"),
    STREAMER('b', "Streamer", "§b§lSTREAMER§b ", Category.NORMAL),
    PARTNER('b', "Partner", "§b§lPARTNER§b ", Category.NORMAL),

    BETA('1', "Beta", "§1§lBETA§1 ", Category.NORMAL),
    LOST('e', "Lost", "§e§lLOST§e ", Category.NORMAL),

    NITRO('d', "Nitro", "§d§lNITRO§d ", Category.SPECIAL),
    CHRISTMAS('c', "Christmas", "§c§lCHRISTMAS§c ", Category.SPECIAL),
    IMMORTAL('5', "Immortal", "§5§lIMMORTAL§5 ", Category.SPECIAL, "imortal"),
    GAMER('e', "Gamer", "§e§lGAMER§e ", Category.SPECIAL),
    VIDALOKA('5', "V1D4LOK4", "§5§lV1D4L0K4§5 ", Category.SPECIAL, "v1d4l0k4"),
    NERD('a', "Nerd", "§a§lNERD§a ", Category.SPECIAL),
    RENEGADO('d', "Renegado", "§d§lRENEGADO§d ", Category.SPECIAL),
    Y2K22('b', "2022", "§b§l2022§b ", Category.SPECIAL),
    WINTER('f', "Winter", "§f§lWINTER§f ", Category.SPECIAL),
    SUMMER('a', "Summer", "§a§lSUMMER§a ", Category.SPECIAL),

    PRO('6', "Pro", "§6§lPRO§6 ", Category.NORMAL),
    MVP('9', "Mvp", "§9§lMVP§9 ", Category.NORMAL),
    VIP('a', "Vip", "§a§lVIP§a ", Category.NORMAL),

    DEFAULT('7', "Padrão", "§7", Category.NORMAL, "normal", "membro");

    public final static char COLOR_CHAR = '\u00a7';

    private char colorId;
    private String formattedName;
    private String prefix;
    private Category category;
    private List<String> aliases;

    Tag(char colorChar, String formattedName, String prefix, Category category, String... aliases) {
        this.colorId = colorChar;
        this.formattedName = formattedName;
        this.prefix = prefix;
        this.category = category;
        this.aliases = Arrays.asList(aliases);
    }

    public String getColouredName(boolean bold) {
        char[] charArray = this.formattedName.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        String prefix = new String(new char[]{COLOR_CHAR, this.colorId}) +
                (bold ? ChatColor.BOLD.toString() : "");
        return prefix + new String(charArray);
    }

    public static Tag fromRank(Rank rank) {
        try {
            return Tag.valueOf(rank.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Tag fromName(String name) {
        try {
            return Tag.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return Stream.of(values()).filter(tag -> tag.formattedName.equals(name) ||
                    tag.aliases.contains(name.toLowerCase())).findFirst().orElse(null);
        }
    }

    public static List<Tag> filterTags(Category category) {
        List<Tag> list = Lists.newArrayList();
        for (Tag tag : values()) {
            if (tag.category != category)
                continue;
            list.add(tag);
        }
        return list;
    }

    public enum Category {

        NORMAL,
        SPECIAL,
        SUBSCRIBER;
    }
}
