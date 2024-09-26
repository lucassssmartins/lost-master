package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.sidebar.GameSidebarModel;
import com.lostmc.hungergames.sidebar.InvincibilitySidebarModel;
import com.lostmc.hungergames.sidebar.PregameSidebarModel;
import com.lostmc.hungergames.stage.GameStage;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListener extends HungerListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String serverId = Commons.getProxyHandler().getLocal().getId();
        Profile profile = Profile.getProfile(p);
        p.setPlayerListHeaderFooter(new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.header")),
                new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.footer")));
        TitleAPI.setTitle(p, "§6§l" + (getMain().getGameMode().isDoubleKit() ? "DOUBLEKIT" : "SINGLEKIT")
                + " #" + serverId.replaceAll("[a-zA-Z0-]", ""), "§eSeja bem vindo!", 10, 10, 10, true);
        Scoreboard board = ScoreboardHandler.getInstance().getScoreboard(p);
        if (getMain().getGameStage().isPregame()) {
            board.setModel(new PregameSidebarModel(board));
        } else if (getMain().getGameStage().isInvincibility()) {
            board.setModel(new InvincibilitySidebarModel(board));
        } else if (getMain().getGameStage().isGametime() || getMain().getGameStage().isEnding()) {
            board.setModel(new GameSidebarModel(board));
        }
    }
}
