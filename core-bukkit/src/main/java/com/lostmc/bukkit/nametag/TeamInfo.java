package com.lostmc.bukkit.nametag;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TeamInfo {

    private final ArrayList<String> members = new ArrayList<>();
    private String name;
    private String prefix;
    private String suffix;
    private boolean visible = true;

    public TeamInfo(String name, String prefix, String suffix) {
        this.name = name.length() > 16 ? name.substring(0, 16) : name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void addMember(String player) {
        if (!members.contains(player)) {
            members.add(player);
        }
    }

    public boolean isSimilar(String name, String prefix, String suffix) {
        return this.name.equals(name) && this.prefix.equals(prefix) && this.suffix.equals(suffix);
    }
}
