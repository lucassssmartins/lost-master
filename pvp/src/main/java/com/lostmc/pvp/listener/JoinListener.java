package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.registry.SpawnWarp;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOW)
    public void join(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player p = event.getPlayer();
        Profile profile = Profile.getProfile(p);
        PvPGamer gamer = (PvPGamer) profile.getResource(Gamer.class);

        p.setHealth(p.getMaxHealth());

        Warp spawnWarp = PvP.getControl().getController(WarpController.class)
                .getWarpByClass(SpawnWarp.class);
        gamer.setWarp(spawnWarp);

        spawnWarp.joinPlayer(p, null);

        p.setPlayerListHeaderFooter(new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.header")),
                new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.footer")));
    }
}
