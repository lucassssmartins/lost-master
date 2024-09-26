package com.lostmc.bukkit.menu;

import com.google.common.collect.Lists;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.tag.Tag;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.lostmc.core.translate.Translator.tl;

public class NametagsMenu extends MenuInventory {

    private static final Set<UUID> cooldownSet = new HashSet<>();

    private GameProfile greenProfile = SocialMediaMenu.createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTYzMjkwMzA2OTM5NywKICAicHJvZmlsZUlkIiA6ICI2MjM5ZWRhM2ExY2Y0YjJiYWMyODk2NGQ0NmNlOWVhOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGYXRGYXRHb2QiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdjNTdlY2M2ZjM0ZmMzNGNkMzUyNGNlMGI3YzFkZDFjNDA1ZjEzMTBlYTI1NDI1ZTcyYzhhNTAyZTk5YWQ1MiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "WQjhjggRDKhclwt85tPRtT7OtdzMdPJUukQqY/jjU5dtQBpIry+AnlVmeqWqAYIOK3a5SvwERlfvzBE7DU0o3k6WS1EAaJQMMfMMkSf5k5mGilzIthuFu2MiOKCMpI8whxYPFri/7wfnWalLvbcZ0UzliKOGWMHIGDkxpyDfqwq+qRRVmjgVOxuGv1T5sZAQwVC0GxoYzF+kL/JiAs8D2zu7a90cyEPAHTF+MoMBnDFcNBaZIgng/bATkZ5ZoypZFn9yGrnk9eSTtkz31pDg5hWLO0vKmq8gMzfdmqHzx0fU3ujemg0MeRfuxXQiyBLfxHbb0HMAZgsSn93f+M3tjNydYiJ2Oy+jNQCaHo8rOe1NWHdEoxEdkp82FFR4mVQncUURKv3OI7T4+7aADSJgJDOUVBtz1fUWoW7fSavNYaujXy3lz6KbdxK/V8CRMv7qViY5yiBgHjQTbGeEMAoygrFx8cyx4hKZJdhyOVWqUs0wZIJk2tGUKSAAuRxP5umlO4Pc26Cqp/aBFo6cmPTvU1HIQkTbQRub/dAJfSwUdZ9idWw1obMEgXqYCCANVBqrwCS7AkGZXhxkDPsZECqVDETkV8Yu/lTyFyE4WWADn4b27X4spCPZLSvvMn0lHITLtqwEEacjc0VbpROG8WS94nhAYd/tk0LcfN64E2QORas=");
    private GameProfile grayProfile = SocialMediaMenu.createSkinProfile("eyJ0aW1lc3RhbXAiOjE1MjY2MTI4ODk4MjIsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U3NzYxODIxMjUyOTc5OGM3MTliNzE2MmE0NzNhNjg1YzQzNTczMjBhODY5NjE2NWU3OTY3OTBiOTBmYmE2NyJ9fX0=",
            "KTnUMhshvKQ+4SYIxvHcu7kTrqw9It+v1oTjW7Xr3aGAGVwNH+z5PLCAY87UOytgoUUAI/mKAIAR8LzOb/x+z84v5lkVCr9pLV9zrzEZTC+jbzkwOMZ5nG1c4YMrh5p5sc7Abnm0dSE/3zLbH9Fev75HuTd9BgqqO1UjRM+sovuO+Gen4ZWRfliT+IFsALPO+3QCWoY2Cay9ZfPT4X7IJrX1GKfl0IiByVA9snyADH8LlwoNwAbe+v++1sy6G36xwrACdqQ9MLBMznpgUJbHlJmxuBsuioPymCAOaQUesRI3Yi053ZfABB+a7wU3tf9h0uNCUoWYr7w7e/N/3wDaphltH8A9MzDc9fFHjcen3+T4Ehl+0MKpY44eWTV6K22vQHuhO4h1c8ruvNTBlimTK27fc3uHhm9TL6ieXqf3UrSqA9bNqfnwHfFVdKXOMZ0cPPit7r6f3PmiVXteE+WijkN8PPzZtfEqU58jPKh3tAo4QzXYEgyGztY9NSGqCfvqBXMYIJgKgUPO3f5aUDmLwI2f1gZvWBsJ+VYHtMonBrIDg5U1bKsSzsQXNZZ+k55Zxe/1i8TEI4YsFGTYGco1UOd1KE+67XaQoPqAPyorNYhWeVmKSiGiHLhFt2RaE1mUf64pKTcyINyXmVlJKIMLIN4yvgAYFREAu/OA1GY6lt8=");

    static {
        Bukkit.getPluginManager().registerEvents(new NametagCooldown(), BukkitPlugin.getInstance());
    }

    public NametagsMenu(Player p, Profile opener, Tag.Category category) {
        super(5 * 9, tl(opener.getLocale(), "menus.nametags.title"));
        reload(this, p, opener, category);
    }

    private void reload(NametagsMenu inventory, Player p, Profile opener, Tag.Category category) {
        inventory.clear();
        setCategoryItens(inventory, opener.getLocale(), category);
        List<Tag> playerTags = hasTags(p, Tag.filterTags(category));
        int max = playerTags.size();
        int index = 1;
        for (Tag tag : playerTags) {
            if (max == 1) {
                inventory.setItem(22, buildItemTag(tag, opener), (p1, inv, type, stack, slot) -> {
                    if (testCooldown(p1))
                        return;
                    cooldownSet.add(p1.getUniqueId());
                    Profile profile = Profile.getProfile(p1);
                    if (profile.getTag() != tag) {
                        BukkitPlugin.getControl().getController(NametagController.class)
                                .setNametag(p1, tag);
                        inv.clear();
                        reload((NametagsMenu) inv, p1, profile, category);
                    } else {
                        p1.sendMessage(tl(profile.getLocale(), "command.tag.already-with-that-tag",
                                tag.getFormattedName()));
                    }
                });
            } else {
                inventory.setItem(convertInd3x(index), buildItemTag(tag, opener), (p1, inv, type, stack, slot) -> {
                    if (testCooldown(p1))
                        return;
                    cooldownSet.add(p1.getUniqueId());
                    Profile profile = Profile.getProfile(p1);
                    if (profile.getTag() != tag) {
                        BukkitPlugin.getControl().getController(NametagController.class)
                                .setNametag(p1, tag);
                        inv.clear();
                        reload((NametagsMenu) inv, p1, profile, category);
                    } else {
                        p1.sendMessage(tl(profile.getLocale(), "command.tag.already-with-that-tag",
                                tag.getFormattedName()));
                    }
                });
                ++index;
            }
        }
        setItem(40, new ItemBuilder(Material.ARROW).setName(tl(opener.getLocale(), "menus.back.name"))
                .setLoreText(tl(opener.getLocale(), "menus.back.lore")).build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                p.openInventory(new MyProfileMenu(p, Profile.getProfile(p)));
            }
        });
    }

    private void setCategoryItens(NametagsMenu menu, Locale locale, Tag.Category current) {
        menu.setItem(3, new ItemBuilder(Material.SKULL_ITEM).setDurability(3)
                .setPlayerHead(current == Tag.Category.NORMAL ? greenProfile : grayProfile)
                .setName(tl(locale, "menus.nametags.category-normal"))
                .setLoreText(current == Tag.Category.NORMAL ?
                        tl(locale, "menus.nametags.already-in-category") :
                        tl(locale, "menus.nametags.click-to-alternate"))
                .build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                reload((NametagsMenu) inv, p, Profile.getProfile(p), Tag.Category.NORMAL);
            }
        });

        menu.setItem(4, new ItemBuilder(Material.SKULL_ITEM).setDurability(3)
                .setPlayerHead(current == Tag.Category.SPECIAL ? greenProfile : grayProfile)
                .setName(tl(locale, "menus.nametags.category-special"))
                .setLoreText(current == Tag.Category.SPECIAL ?
                        tl(locale, "menus.nametags.already-in-category") :
                        tl(locale, "menus.nametags.click-to-alternate"))
                .build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                reload((NametagsMenu) inv, p, Profile.getProfile(p), Tag.Category.SPECIAL);
            }
        });

        menu.setItem(5, new ItemBuilder(Material.SKULL_ITEM).setDurability(3)
                .setPlayerHead(current == Tag.Category.SUBSCRIBER ? greenProfile : grayProfile)
                .setName(tl(locale, "menus.nametags.category-subscriber"))
                .setLoreText(current == Tag.Category.SUBSCRIBER ?
                        tl(locale, "menus.nametags.already-in-category") :
                        tl(locale, "menus.nametags.click-to-alternate"))
                .build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                reload((NametagsMenu) inv, p, Profile.getProfile(p), Tag.Category.SUBSCRIBER);
            }
        });
    }

    public boolean testCooldown(Player p) {
        if (cooldownSet.contains(p.getUniqueId())) {
            p.sendMessage(tl(Profile.getProfile(p).getLocale(), "menus.nametag.in-cooldown"));
            return true;
        }
        return false;
    }

    private int convertInd3x(int next) {
        if (next >= 1 && next <= 7) {
            return (next + 9);
        } else if (next >= 8 && next <= 14) {
            return (next + 11);
        } else {
            return (next + 13);
        }
    }

    private ItemStack buildItemTag(Tag tag, Profile profile) {
        ItemBuilder nametag = new ItemBuilder(Material.INK_SACK);
        if (tag == profile.getTag())
            nametag.setDurability(1);
        else
            nametag.setDurability(10);
        return nametag.setName(tag.getPrefix() + profile.getName())
                .setLoreText(tl(profile.getLocale(), tag == profile.getTag() ? "menus.nametags.current-selected" : "menus.nametags.click-to-select"))
                .build();
    }

    private List<Tag> hasTags(Player p, List<Tag> list) {
        Iterator<Tag> it = list.iterator();
        while (it.hasNext()) {
            Tag next = it.next();
            if (p.hasPermission("tag." + next.toString().toLowerCase()))
                continue;
            it.remove();
            continue;
        }
        return list;
    }

    private List<Tag> getPlayerTags(Player p) {
        List<Tag> list = Lists.newArrayList();
        for (Tag tag : Tag.values()) {
            if (tag != Tag.DEFAULT && !p.hasPermission("tag." + tag.toString().toLowerCase()))
                continue;
            list.add(tag);
        }
        return list;
    }

    private static class NametagCooldown implements Listener {

        @EventHandler
        public void timer(ServerTimerEvent event) {
            if (event.getCurrentTick() % 30 != 0)
                return;
            cooldownSet.clear();
        }
    }
}
