package com.lostmc.game.event.player;

import com.lostmc.game.constructor.Kit;
import com.lostmc.game.event.GameEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
@RequiredArgsConstructor
public class PlayerSelectKitEvent extends GameEvent implements Cancellable {

    private final Player player;
    private final Kit kit;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
