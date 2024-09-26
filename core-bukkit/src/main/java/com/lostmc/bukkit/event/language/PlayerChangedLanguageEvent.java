package com.lostmc.bukkit.event.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public class PlayerChangedLanguageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Locale oldLocale;
    private final Locale newLocale;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
