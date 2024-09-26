package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.event.vanish.PlayerUnvanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.scheduler.GameScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class VanishListener extends HungerListener {

    public boolean isVanished(Player p) {
        return HungerGames.getControl().getController(VanishController.class).isVanished(p);
    }

    @EventHandler
    public void onVanishMode(PlayerVanishModeEvent event) {
        Player p = event.getPlayer();
        HungerGamer gamer = HungerGamer.getGamer(p);
        if (event.getMode() == PlayerVanishModeEvent.Mode.VANISH) {
            if (getMain().getGameStage().isPregame() || getMain().getGameStage().isInvincibility()) {
                if (gamer.isAlive()) {
                    if (p.hasPermission("core.cmd.admin")) {
                        gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
                    } else {
                        gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
                    }
                }
            } else if (getMain().getGameStage().isInvincibility()) {
                if (p.hasPermission("core.cmd.admin")) {
                    gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
                } else {
                    gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
                }
            } else if (getMain().getGameStage().isGametime()) {
                if (gamer.isAlive()) {
                    GameScheduler.killPlayer(p);
                }
                if (p.hasPermission("core.cmd.admin")) {
                    gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
                } else {
                    gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
                }
            }
        } else if (event.getMode() == PlayerVanishModeEvent.Mode.PLAYER) {
            if (getMain().getGameStage().isPregame()
                    || getMain().getGameStage().isInvincibility()) {
                if (gamer.isNotPlaying()) {
                    gamer.setGamerState(HungerGamer.GamerState.ALIVE);
                }
            } else if (getMain().getGameStage().isGametime() || getMain().getGameStage().isEnding()) {
                if (p.hasPermission("core.cmd.admin")) {
                    gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
                } else {
                    gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
                }
            }
        }
        if (getMain().getGameStage().isGametime())
            ((HungerGamesMode) getMain().getGameMode()).checkWinner();
        for (Player ps : Bukkit.getOnlinePlayers()) {
            if (!ps.equals(p)) {
                ps.showPlayer(p);
                if (HungerGamer.getGamer(ps).isNotPlaying()) {
                    if (HungerGames.getControl().getController(VanishController.class)
                            .isVanished(ps)) {
                        if (Profile.getProfile(ps).getRank().ordinal() <
                                Profile.getProfile(p).getRank().ordinal())
                            continue;
                    }
                    p.showPlayer(ps);
                }
                if (HungerGamer.getGamer(ps).isAlive() && !p.canSee(ps))
                    p.showPlayer(ps);
            }
        }
    }

    @EventHandler
    public void onUnvanish(PlayerUnvanishEvent event) {
        HungerGamer viewer = HungerGamer.getGamer(event.getViewer());
        HungerGamer target = HungerGamer.getGamer(event.getPlayer());
        if (viewer.isHiddingSpecs() && (target.isNotPlaying() || isVanished(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        HungerGamer viewer = HungerGamer.getGamer(event.getViewer());
        HungerGamer target = HungerGamer.getGamer(event.getPlayer());
        if (target.isAlive()) {
            event.setCancelled(true);
        } else if (!viewer.isHiddingSpecs() && (target.isNotPlaying())) {
            if (HungerGames.getControl().getController(VanishController.class)
                    .isVanished(event.getPlayer()) && Profile.getProfile(event.getPlayer())
                    .getRank().ordinal() < Profile.getProfile(event.getViewer()).getRank().ordinal())
                return;
            event.setCancelled(true);
        }
    }
}
