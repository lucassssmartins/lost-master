package com.lostmc.pvp.ability.registry.controllers;

import com.lostmc.pvp.ability.registry.fight.GladiatorFight;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GladiatorFightController {

    private List<GladiatorFight> playersInFight;
    private List<Block> fightsBlocks;

    public GladiatorFightController() {
        playersInFight = new ArrayList<>();
        fightsBlocks = new ArrayList<>();
    }

    public void stop() {
        for (Block b : fightsBlocks) {
            b.setType(Material.AIR);
        }
    }

    public boolean isInFight(Player p) {
        return getFight(p) != null;
    }

    public void removeFromFight(GladiatorFight id) {
        playersInFight.remove(id);
    }

    public void addToFights(GladiatorFight id) {
        playersInFight.add(id);
    }

    public GladiatorFight getFight(Player p) {
        for (GladiatorFight id : playersInFight) {
            if (id.isIn1v1(p)) {
                return id;
            }
        }
        return null;
    }

    public void removeBlock(Block b) {
        fightsBlocks.remove(b);
    }

    public void addBlock(Block b) {
        fightsBlocks.add(b);
    }

    public boolean isFightBlock(Block b) {
        return fightsBlocks.contains(b);
    }
}
