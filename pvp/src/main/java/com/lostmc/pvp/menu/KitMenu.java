package com.lostmc.pvp.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.kit.KitMenuType;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.ability.KitController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.lostmc.core.translate.Translator.tl;

public class KitMenu extends MenuInventory {

    public KitMenu(Player player, Profile profile, int page, KitMenuType menuType) {
        super(6 * 9, tl(profile.getLocale(), "menu.pvp-kits.title", menuType.getId()));
        buildItems(this, player, page, profile, menuType);
    }

    public void buildItems(MenuInventory menu, Player player, int page, Profile opener, KitMenuType menuType) {
        menu.clear();

        int itemsPerPage = page > 1 ? 21 : 20;

        List<Kit> reports = PvP.getControl().getController(KitController.class).getKits();
        int pageStart = 0;
        int pageEnd = itemsPerPage;
        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }
        if (pageEnd > reports.size()) {
            pageEnd = reports.size();
        }
        if (page != 1) {
            menu.setItem(53, new ItemBuilder(Material.ARROW).setDurability(10)
                            .setName(tl(opener.getLocale(), "menu.pvp-kits.previous-page",
                                    (page - 1))).build(),
                    new MenuClickHandler() {

                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, p, (page - 1), Profile.getProfile(p), menuType);
                        }
                    });
        }
        if (Math.ceil(reports.size() / itemsPerPage) + 1 > page) {
            menu.setItem(45, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName(tl(opener.getLocale(), "menu.pvp-kits.next-page", (page + 1))).build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, p, (page + 1), Profile.getProfile(p), menuType);
                        }
                    });
        }

        int w;
        if ((w = (page > 1 ? 1 : 2)) > 1) {
            setItem(10, new ItemBuilder(Material.BARRIER).setName(tl(opener.getLocale(), "menu.pvp-kits.none-ability-name"))
                            .setLoreText(tl(opener.getLocale(), "menu.pvp-kits.none-ability-lore")).build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            p.closeInventory();
                            Profile profile = Profile.getProfile(p);
                            Gamer gamer = profile.getResource(Gamer.class);
                            gamer.getKits().remove(menuType.getId());
                            p.sendMessage(tl(profile.getLocale(), menuType.getId() == 1 ?
                                    "menu.pvp-kits.removed-primary" : "menu.pvp-kits.removed-secondary"));
                        }
                    });
        }

        for (int i = pageStart; i < pageEnd; i++) {
            Kit ability = reports.get(i);

            if (!player.hasPermission("pvpkit" + menuType.getId() + "." + ability.getName().toLowerCase())) {
                setItem(convertInd3x(w), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(14)
                                .setName(tl(opener.getLocale(), "menu.pvp-kits.no-permission-kit-name", ability.getName()))
                                .setLoreText(tl(opener.getLocale(), "menu.pvp-kits.no-permission-kit-lore",
                                        ability.getDescription())).build(),
                        new MenuClickHandler() {
                            @Override
                            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                                p.closeInventory();
                                p.sendMessage(tl(Profile.getProfile(p).getLocale(), menuType.getId() == 1 ? "menu.pvp-kits.dont-have-primary-ability"
                                        : "menu.pvp-kits.dont-have-secondary-ability", ability.getName()));
                            }
                        });
            } else {
                setItem(convertInd3x(w), new ItemBuilder(ability.getIconMaterial())
                                .setName(tl(opener.getLocale(), "menu.pvp-kits.kit-name", ability.getName()))
                                .setLoreText(tl(opener.getLocale(), "menu.pvp-kits.kit-lore",
                                        ability.getDescription())).build(),
                        new MenuClickHandler() {
                            @Override
                            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                                p.closeInventory();
                                Profile profile = Profile.getProfile(p);
                                Gamer gamer = profile.getResource(Gamer.class);
                                if (gamer.getKits().values().contains(ability)) {
                                    p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.already-selected"));
                                } else {
                                    Kit other = gamer.getKits().get(menuType.getId() == 1 ? 2 : 1);
                                    if (other == null || !ability.isIncompatible(other)) {
                                        gamer.getKits().put(menuType.getId(), ability);
                                        TitleAPI.setTitle(p, tl(profile.getLocale(), "menu.pvp-kits.selected-title", ability.getName()),
                                                tl(profile.getLocale(), "menu.pvp-kits.selected-subtitle"), 10, 10, 10, true);
                                        p.sendMessage(tl(profile.getLocale(), menuType.getId() == 1 ? "menu.pvp-kits.selected-primary-ability"
                                                : "menu.pvp-kits.selected-secondary-ability", ability.getName()));
                                    } else {
                                        p.sendMessage("§cO kit " + ability.getName() + " não é compatível com o kit "
                                                + other.getName() + ".");
                                    }
                                }
                            }
                        });
            }
            w += 1;
        }
    }

    private int convertInd3x(int next) {
        if (next >= 1 && next <= 7) {
            return (next + 9);
        } else if (next >= 8 && next <= 14) {
            return (next + 11);
        } else if (next >= 15 && next <= 21) {
            return (next + 13);
        } else {
            return (next + 15);
        }
    }
}
