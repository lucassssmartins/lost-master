package com.lostmc.hungergames.kit.registry;

import com.lostmc.game.GamePlugin;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Nenhum extends HungerKit {

    public Nenhum(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.BARRIER);
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }
}
