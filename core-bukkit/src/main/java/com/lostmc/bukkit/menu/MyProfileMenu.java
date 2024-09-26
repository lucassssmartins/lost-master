package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.translate.Translator;
import com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.lostmc.core.translate.Translator.tl;

public class MyProfileMenu extends MenuInventory {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static GameProfile planetProfile = SocialMediaMenu.createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTYyMDE0NDkwMzk5NCwKICAicHJvZmlsZUlkIiA6ICIxNGM5MDFmYWMyNDU0NmZlYjYzODcyYjI1ZjE1MDY3NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFYXJ0aCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZjU5MjFjOTNhMDA2Y2FhNTc0YzNhNjQyYmU0MzE3MmQzNTRkNzYzMTI3MzU2ZTNlNDExNzllYTczODUyNTRjIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NTNjYWM4Yjc3OWZlNDEzODNlNjc1ZWUyYjg2MDcxYTcxNjU4ZjIxODBmNTZmYmNlOGFhMzE1ZWE3MGUyZWQ2IgogICAgfQogIH0KfQ==",
            "GA3WnPHWlKwPd5uNmGzxM8qlto1BIl4Khh/yr5v2uPdoi+OPEbBawGWA7xHMu6Qq0BwrrNrb/YKHghBF90uHu9R8eGKyc4DBhUo2l5NZe020HtER7T8J77SYg9TkNtVinUnVEw2qYEitMR6fx8aiE5Bd+XS+on5vvEaqKGN77lPlqdiPiAhnG/bCCXOaiI5U6CR+Z6pxlvmQABSGznPvubuhONldIsPO5yNV1pSD3x11tei/X/fuWxIENcW2yE4KOEmtfbsxYpRMGde+2RbBKddkU6jZtvJA7w6MSHnzAerTwBPSatQtVar1/r7LY6Uq5L73dK1YefD20a0B10lSQKTw28TBblrJWg4YfGM/3aCKMbgmcgPojWaolr70KsGACGiQoegqtjqs5Xmgi47j8xGxS4aaGKmxZdSbfi3UE5R8o23EJY+SCRyPC/JhcyZafQyc8Dj0LZS7etXE4+G0Mks+0LWQOW4CNaFd+Dki+UlPdtVAP0cYsLtHzZRwxVxKPgX7qgNSmq41jaaXczFHqCDEWctdXSx+2s7Su4xHyok5k3zJSAvEdvnTQIUkA7b1mYHo7l/hPO5GUxpQa32fobCnqvmfJNtkph49Uev/S8zae8t0V+MJ2mKFKIgQG4PA2yyTxmS8erApim8ljg/vurLt1y5gQvoBTmPIaQ/G7Y0=");

    public MyProfileMenu(Player p, Profile opener) {
        super(5 * 9, Translator.tl(opener.getLocale(), "menu.my-profile.title"));

        ItemBuilder socialMediasItem = new ItemBuilder(Material.SKULL_ITEM).setDurability(3)
                .setPlayerHead(SocialMediaMenu.twitterProfile).setName(Translator.tl(opener.getLocale(),
                        "menu.my-profile.social-media-name"))
                .setLoreText(tl(opener.getLocale(), "menu.my-profile.social-media-lore"));
        setItem(12, socialMediasItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                Profile profile = Profile.getProfile(p);
                SocialMediaMenu menu = new SocialMediaMenu(profile, profile, false);
                menu.setItem(49, new ItemBuilder(Material.ARROW).setName(tl(opener.getLocale(), "menus.back.name"))
                        .setLoreText(tl(opener.getLocale(), "menus.back.lore")).build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        p.openInventory(new MyProfileMenu(p, Profile.getProfile(p)));
                    }
                });
                p.openInventory(menu);
            }
        });

        String generalRank = tl(opener.getLocale(), "menu.my-profile.general-rank",
                Tag.fromRank(opener.getRank()).getColouredName(false));
        String profileLocale = tl(opener.getLocale(), "menu.my-profile.profile-locale",
                ChatColor.stripColor(tl(opener.getLocale(), "menu.lang.item-locale-name")));
        String coins = tl(opener.getLocale(), "menu.my-profile.total-coins",
                String.format("%,d", opener.getData(DataType.COINS).getAsInt()));
        String achievements = tl(opener.getLocale(), "menu.my-profile.total-achievements", 0);
        String firstLoggedIn = tl(opener.getLocale(), "menu.my-profile.first-logged-in",
                formatter.format(new Date(opener.getData(DataType.FIRST_LOGGED_IN).getAsLong())));
        String lastLoggedIn = tl(opener.getLocale(), "menu.my-profile.last-logged-in",
                formatter.format(new Date(opener.getData(DataType.LAST_LOGGED_IN).getAsLong())));

        GameProfile playerProfile = new GameProfile(UUID.randomUUID(), null);
        try {
            playerProfile.getProperties().put("textures",
                    ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next());
        } catch (Exception noPropertyFound) {
        }

        ItemBuilder skullItem = new ItemBuilder(Material.SKULL_ITEM)
                .setPlayerHead(playerProfile).setDurability(3)
                .setName("§a" + opener.getName()).setLoreText(
                        generalRank + "\n" + profileLocale + "\n" + coins + "\n" +
                                achievements + "\n" + firstLoggedIn + "\n" + lastLoggedIn
                );
        setItem(13, skullItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

            }
        });

        ItemBuilder statisticItem = new ItemBuilder(Material.PAPER).setName(tl(opener.getLocale(),
                "menu.my-profile.statistics-name")).setLoreText(tl(opener.getLocale(),
                "menu.my-profile.statistics-lore"));
        setItem(14, statisticItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

            }
        });

        ItemBuilder medalsItem = new ItemBuilder(Material.NAME_TAG).setName(tl(opener.getLocale(),
                "menu.my-profile.medals-name")).setLoreText(tl(opener.getLocale(), "menu.my-profile.medals-lore"));
        setItem(20, medalsItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

            }
        });

        ItemBuilder preferencesItem = new ItemBuilder(Material.getMaterial(356)).setName(tl(opener.getLocale(),
                "menu.my-profile.preferences-name")).setLoreText(tl(opener.getLocale(),
                "menu.my-profile.preferences-lore"));
        setItem(21, preferencesItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                PreferencesMenu menu = new PreferencesMenu(Profile.getProfile(p));
                menu.setItem(49, new ItemBuilder(Material.ARROW).setName(tl(opener.getLocale(), "menus.back.name"))
                                .setLoreText(tl(opener.getLocale(), "menus.back.lore")).build(),
                        (p13, inv1, type1, stack1, slot1)
                                -> p13.openInventory(new MyProfileMenu(p13, Profile.getProfile(p13))));
                p.openInventory(menu);
            }
        });

        ItemBuilder nametagItem = new ItemBuilder(Material.GOLD_INGOT).setName(tl(opener.getLocale(),
                "menu.my-profile.nametags-name")).setLoreText(tl(opener.getLocale(),
                "menu.my-profile.nametags-lore"));
        setItem(22, nametagItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                Profile profile = Profile.getProfile(p);
                p.openInventory(new NametagsMenu(p, profile, profile.getTag().getCategory()));
            }
        });

        ItemBuilder skinsItem = new ItemBuilder(Material.ITEM_FRAME).setName(tl(opener.getLocale(),
                "menu.my-profile.skins-name")).setLoreText(tl(opener.getLocale(), "menu.my-profile.skins-lore"));
        setItem(23, skinsItem.build(), (p1, inv, type, stack, slot) -> {

        });

        ItemBuilder langItem = new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(planetProfile).setName(
                tl(opener.getLocale(), "menu.my-profile.lang-name")).setLoreText(tl(opener.getLocale(),
                "menu.my-profile.lang-lore"));
        setItem(24, langItem.build(), (p12, inv, type, stack, slot) -> {
            LanguageMenu menu = new LanguageMenu(Profile.getProfile(p12).getLocale());
            menu.setItem(22, new ItemBuilder(Material.ARROW).setName(tl(opener.getLocale(), "menus.back.name"))
                    .setLoreText(tl(opener.getLocale(), "menus.back.lore")).build(),
                    (p13, inv1, type1, stack1, slot1)
                            -> p13.openInventory(new MyProfileMenu(p13, Profile.getProfile(p13))));
            p12.openInventory(menu);
        });
    }
}
