package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.core.Commons;
import com.lostmc.core.utils.mojang.UUIDFetcher;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PreLoginStartListener extends ProxyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent event) {
        event.registerIntent(getPlugin());
        Commons.getPlatform().runAsync(() -> {
            if (Commons.isSystemReady()) {
                PendingConnection connection = event.getConnection();
                if (connection.getName().matches("[a-zA-Z0-9_]{3,16}")) {
                    UUIDFetcher uuidFetcher = Commons.getUuidFetcher();
                    if (uuidFetcher.getUUIDOf(connection.getName()) != null) {
                        connection.setOnlineMode(true);
                    } else {
                        connection.setOnlineMode(false);
                        connection.setUniqueId(uuidFetcher.getFixedOfflineUUID(connection.getName()));
                    }
                } else {
                    event.setCancelled(true);
                    event.setCancelReason(TextComponent.fromLegacyText("§cILLEGAL_NICKNAME"));
                }
            } else {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText("§cSYSTEM_INITIALIZING"));
            }
            event.completeIntent(getPlugin());
        });
    }
}
