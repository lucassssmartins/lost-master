package com.lostmc.game.event.combatlog;

import com.lostmc.game.event.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class PlayerCombatLogEvent extends GameEvent {

    private final Player player;
    private final Player combatPlayer;
    private int seconds;
}
