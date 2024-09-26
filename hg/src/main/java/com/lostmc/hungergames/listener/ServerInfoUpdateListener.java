package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.event.server.ServerInfoUpdateEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.minigame.HungerGamesServer;
import com.lostmc.core.server.minigame.MinigameState;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.manager.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;

public class ServerInfoUpdateListener extends HungerListener {

    @EventHandler
    public void onServerInfoUpdate(ServerInfoUpdateEvent event) {
        ProxiedServer localServer = event.getLocalhost();

        HashMap<UUID, String> alivePlayers = new HashMap<>();
        for (HungerGamer gamer : Management.getManagement(GamerManager.class).getAliveGamers()) {
            Player p = Bukkit.getPlayer(gamer.getUniqueId());
            if (p != null && p.isOnline()) {
                alivePlayers.put(p.getUniqueId(), p.getName());
            }
        }

        HungerGamesServer hgServer = new HungerGamesServer(localServer.getId(), alivePlayers, Bukkit.getMaxPlayers(),
                Bukkit.hasWhitelist());
        hgServer.setServerType(localServer.getServerType());
        hgServer.setStarted(Commons.isSystemReady());
        hgServer.setTime(getMain().getTimer());
        hgServer.setState(MinigameState.valueOf(getMain().getGameStage().toString()));
        hgServer.setDoubleKit(getMain().getGameMode().isDoubleKit());

        event.setLocalhost(hgServer);
    }
}
