package com.lostmc.game.event.combatlog;

import com.lostmc.game.event.GameEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class PlayerDeathOutOfCombatEvent extends GameEvent {

    private final Player player;
}
