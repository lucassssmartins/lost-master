package com.lostmc.game.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
@Getter
public enum KitRarity {

    MYSTIC(ChatColor.DARK_RED), //
    LEGENDARY(ChatColor.RED), //
    EPIC(ChatColor.DARK_PURPLE), //
    RARE(ChatColor.GOLD), //
    COMMON(ChatColor.RESET);

    private ChatColor color;
}
