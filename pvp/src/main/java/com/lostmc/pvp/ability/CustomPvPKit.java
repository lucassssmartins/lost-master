package com.lostmc.pvp.ability;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import org.bukkit.entity.Player;

public abstract class CustomPvPKit extends Kit {

    public CustomPvPKit(GamePlugin plugin) {
        super(plugin);
    }

    public PvP getPlugin() {
        return (PvP) super.getPlugin();
    }

    @Override
    public boolean canUse(Player player) {
        PvPGamer gamer = (PvPGamer) Profile.getProfile(player).getResource(Gamer.class);
        Warp warp = gamer.getWarp();
        if (warp == null)
            return false;
        return warp instanceof ArenaWarp;
    }
}
