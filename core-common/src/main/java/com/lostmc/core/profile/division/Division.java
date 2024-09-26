package com.lostmc.core.profile.division;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Division {

    private ChatColor color;
    private String name, modelName;
    private String symbol;
    private int minElo;
    private int maxElo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Division) {
            Division that = (Division) o;
            return that.name.equals(this.name) && that.modelName.equals(this.modelName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modelName);
    }
}
