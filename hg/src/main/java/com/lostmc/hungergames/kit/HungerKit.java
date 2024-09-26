package com.lostmc.hungergames.kit;

import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Kit;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class HungerKit extends Kit {

    public HungerKit(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public HungerGames getPlugin() {
        return (HungerGames) super.getPlugin();
    }

    @Override
    public boolean isMainItem(ItemStack item) {
        if (item == null || getMainItem() == null)
            return false;
        return getMainItem().getType().equals(item.getType());
    }

    @Override
    public boolean isUsing(Player p) {
        return canUse(p) && HungerGamer.getGamer(p).getKits().values().contains(this) && isActived();
    }

    @Override
    public boolean canUse(Player player) {
        HungerGamer gamer = HungerGamer.getGamer(player);
        return gamer.isAlive();
    }
}
