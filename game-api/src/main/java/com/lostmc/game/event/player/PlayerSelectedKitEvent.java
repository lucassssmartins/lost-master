package com.lostmc.game.event.player;

import com.lostmc.game.constructor.Kit;
import com.lostmc.game.event.GameEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class PlayerSelectedKitEvent extends GameEvent {

    private final Player player;
    private final Kit kit;
}
