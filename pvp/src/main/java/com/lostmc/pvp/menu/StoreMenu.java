package com.lostmc.pvp.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketOutAddPermission;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.utils.DateUtils;
import com.lostmc.game.constructor.Kit;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.ability.KitController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static com.lostmc.core.translate.Translator.tl;

public class StoreMenu extends MenuInventory {

    private static int itemsPerPage = 21;

    public StoreMenu(Player player, Profile profile, int page) {
        super(6 * 9, tl(profile.getLocale(), "menu.pvp-kit-store.title", page));
        buildItems(this, player, page, profile);
    }

    public void buildItems(MenuInventory menu, Player player, int page, Profile opener) {
        menu.clear();

        List<Kit> reports = getAbilities(player);
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
                            buildItems((MenuInventory) inv, p, (page - 1), Profile.getProfile(p));
                        }
                    });
        }
        if (Math.ceil(reports.size() / itemsPerPage) + 1 > page) {
            menu.setItem(45, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName(tl(opener.getLocale(), "menu.pvp-kits.next-page", (page + 1))).build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, p, (page + 1), Profile.getProfile(p));
                        }
                    });
        }

        int w = 1;
        for (int i = pageStart; i < pageEnd; i++) {
            Kit ability = reports.get(i);
            setItem(convertInd3x(w), new ItemBuilder(ability.getIconMaterial())
                            .setName(tl(opener.getLocale(), "menu.pvp-kits.kit-store-name", ability.getName()))
                            .setLoreText(tl(opener.getLocale(), "menu.pvp-kits.kit-store-lore",
                                    ability.getDescription())).build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            p.closeInventory();
                            Profile profile = Profile.getProfile(p);
                            if (p.hasPermission("pvpkit2." + ability.getName().toLowerCase())) {
                                p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.already-owned-kit"));
                            } else if (type == ClickType.LEFT) {
                                if (profile.getData(DataType.COINS).getAsInt() >= ability.getBuyPrice()) {
                                    profile.setData(DataType.COINS, (profile.getData(DataType.COINS).getAsInt() - ability.getBuyPrice()));
                                    Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                            Commons.getGson().toJson(
                                                    new PacketUpdateSingleData(profile.getUniqueId(), DataType.COINS, profile.getData(DataType.COINS))));
                                    for (int i = 1; i <= 2; i++) {
                                        PermissionProduct permission = new PermissionProduct("pvpkit" + i + "." + ability.getName(),
                                                "ability.bought-in-server", Commons.CONSOLE_UNIQUEID, "CONSOLE", false,
                                                -1);

                                        profile.addPermission(permission);

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                Commons.getRedisBackend().publish(PacketType.UPDATE_IN_CUSTOM_PERMISSIONS.toString(),
                                                        Commons.getGson().toJson(new PacketOutAddPermission(p.getUniqueId(), permission)));
                                            }
                                        }.runTaskLater(PvP.getInstance(), (i * 1L));
                                    }
                                    profile.save();
                                    TitleAPI.setTitle(p, tl(profile.getLocale(), "menu.pvp-kits.acquired-kit-title", ability.getName()),
                                            tl(profile.getLocale(), "menu.pvp-kits.acquired-kit-subtitle"), 10, 10, 10, true);
                                    p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.bought-kit", ability.getName()));
                                } else {
                                    p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.not-enought-to-buy",
                                            (ability.getBuyPrice() - profile.getData(DataType.COINS).getAsInt()), ability.getName()));
                                }
                            } else if (type == ClickType.RIGHT) {
                                if (profile.getData(DataType.COINS).getAsInt() >= ability.getRentPrice()) {
                                    profile.setData(DataType.COINS, (profile.getData(DataType.COINS).getAsInt() - ability.getRentPrice()));
                                    Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                            Commons.getGson().toJson(
                                                    new PacketUpdateSingleData(profile.getUniqueId(), DataType.COINS, profile.getData(DataType.COINS))));
                                    for (int i = 1; i <= 2; i++) {
                                        PermissionProduct permission = new PermissionProduct("pvpkit" + i + "." + ability.getName(),
                                                "ability.rented-in-server", Commons.CONSOLE_UNIQUEID, "CONSOLE", false,
                                                parseLong("3d"));

                                        profile.addPermission(permission);

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                Commons.getRedisBackend().publish(PacketType.UPDATE_IN_CUSTOM_PERMISSIONS.toString(),
                                                        Commons.getGson().toJson(new PacketOutAddPermission(p.getUniqueId(), permission)));
                                            }
                                        }.runTaskLater(PvP.getInstance(), (i * 1L));
                                    }
                                    profile.save();
                                    TitleAPI.setTitle(p, tl(profile.getLocale(), "menu.pvp-kits.acquired-kit-title", ability.getName()),
                                            tl(profile.getLocale(), "menu.pvp-kits.acquired-kit-subtitle"), 10, 10, 10, true);
                                    p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.rented-kit", ability.getName()));
                                } else {
                                    p.sendMessage(tl(profile.getLocale(), "menu.pvp-kits.not-enought-to-rent",
                                            (ability.getRentPrice() - profile.getData(DataType.COINS).getAsInt()), ability.getName()));
                                }
                            }
                        }
                    });
            w += 1;
        }
    }

    private long parseLong(String time) {
        try {
           return DateUtils.parseDateDiff(time);
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Kit> getAbilities(Player player) {
        return PvP.getControl().getController(KitController.class).getKits();
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
