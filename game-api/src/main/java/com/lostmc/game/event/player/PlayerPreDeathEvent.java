package com.lostmc.game.event.player;

import com.lostmc.game.event.GameEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
@RequiredArgsConstructor
public class PlayerPreDeathEvent extends GameEvent implements Cancellable {

    private final Player player;
    private final Entity killer;
    private final EntityDamageEvent.DamageCause lastDamageCause;
    private boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
