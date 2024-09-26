package com.lostmc.core.translate;

import com.lostmc.core.Commons;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Translator {

    private static final Map<Locale, BundleContainer> bundleMap = new HashMap<>();

    public static void loadTranslations() {
        File folder = new File(System.getProperty("user.home") + "\\Desktop\\lang");
        if (!folder.exists())
            folder.mkdirs();
        for (Locale locale : Commons.AVAILABLE_LOCALES) {
            bundleMap.put(locale, new BundleContainer(folder, locale));
        }
    }

    public static void reloadTranslations() {
        bundleMap.clear();
        loadTranslations();
    }

    public static String tl(Locale locale, String key, Object... format) {
        BundleContainer container = bundleMap.get(locale);
        if (container == null)
            return "[NOT MAPPED: '" + key + "', '" + locale + "']";
        return ChatColor.translateAlternateColorCodes('&', container.translate(key, format));
    }
}
