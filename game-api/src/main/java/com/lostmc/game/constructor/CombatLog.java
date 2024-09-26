package com.lostmc.game.constructor;

import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CombatLog {

    @NonNull
    private UUID owner;
    private UUID combatLogged;
    private long time;

    public void logout() {
        this.combatLogged = null;
        this.time = 0L;
    }

    public boolean isLogged() {
        return this.combatLogged != null && this.time >= System.currentTimeMillis();
    }

    public void customHit(Player player, int log) {
        this.combatLogged = player.getUniqueId();
        this.time = System.currentTimeMillis() + (log * 1000L);
    }

    public void hit(Player combatPlayer) {
        Player player = Bukkit.getPlayer(this.owner);
        if (player == null)
            return;
        PlayerCombatLogEvent event = new PlayerCombatLogEvent(player, combatPlayer, 10);
        Bukkit.getPluginManager().callEvent(event);
        this.combatLogged = event.getCombatPlayer().getUniqueId();
        this.time = System.currentTimeMillis() + (event.getSeconds() * 1000L);
    }
}
