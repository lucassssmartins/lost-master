package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.event.language.PlayerChangedLanguageEvent;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateInLanguage;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class LanguageMenu extends MenuInventory {

    private static final Set<UUID> cooldownSet = new HashSet<>();

    static {
        Bukkit.getPluginManager().registerEvents(new CooldownListener(), BukkitPlugin.getInstance());
    }

    public LanguageMenu(Locale locale) {
        super(3 * 9, Translator.tl(locale, "menu.lang.title"));
        int i = 10;
        reload(this, locale);
    }

    private void reload(LanguageMenu menu, Locale locale) {
        int i = 10;
        for (Locale available : Commons.AVAILABLE_LOCALES) {
            menu.setItem(i, new ItemBuilder(locale.equals(available) ? Material.ENCHANTED_BOOK : Material.BOOK).
                    setName(Translator.tl(available, "menu.lang.item-locale-name"))
                    .setLoreText(Translator.tl(available, "menu.lang.item-lore")).build(),
                    (p, inv, type, stack, slot) -> {
                        Profile profile = Profile.getProfile(p);
                        if (cooldownSet.contains(p.getUniqueId())) {
                            p.sendMessage(Translator.tl(profile.getLocale(), "menu.lang.in-cooldown"));
                            return;
                        }
                        Locale previous = profile.getLocale();
                        if (!previous.equals(available)) {
                            cooldownSet.add(p.getUniqueId());
                            profile.setLocale(available);
                            profile.save();
                            Bukkit.getPluginManager().callEvent(new PlayerChangedLanguageEvent(p, previous, available));
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_LANGUAGE.toString(),
                                    new PacketUpdateInLanguage(p.getUniqueId(), available).toJson());
                            p.sendMessage(Translator.tl(profile.getLocale(), "menu.lang.changed-your-language"));
                            reload((LanguageMenu) inv, available);
                        } else {
                            p.sendMessage(Translator.tl(profile.getLocale(), "menu.lang.current-language"));
                        }
                    });
            ++i;
        }
    }

    private static class CooldownListener implements Listener {

        @EventHandler
        public void timer(ServerTimerEvent event) {
            if (event.getCurrentTick() % 40 != 0)
                return;
            cooldownSet.clear();
        }
    }
}
