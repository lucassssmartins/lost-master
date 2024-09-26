package com.lostmc.hungergames.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.manager.KitManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitMenu extends MenuInventory {

    private static int itemsPerPage = 28;

    public KitMenu(Player p, boolean doubleKit) {
        super(6 * 9, doubleKit ? "§8Seletor kit secundário" : "§8Seletor kit primário");
        buildItems(p, doubleKit, this, 1);
    }

    public void buildItems(Player opener, boolean doubleKit, MenuInventory menu, int page) {
        menu.clear();

        KitManager manager = Management.getManagement(KitManager.class);

        List<Kit> kits = manager.getAllKits();
        int pageStart = 0;
        int pageEnd = itemsPerPage;
        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }
        if (pageEnd > kits.size()) {
            pageEnd = kits.size();
        }
        if (page != 1) {
            menu.setItem(0, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName("§aPágina anterior").build(),
                    new MenuClickHandler() {

                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems(p, doubleKit, (MenuInventory) inv, (page - 1));
                        }
                    });
        }
        if (Math.ceil(kits.size() / itemsPerPage) + 1 > page) {
            menu.setItem(8, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName("§aPróxima página").build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems(p, doubleKit, (MenuInventory) inv, (page + 1));
                        }
                    });
        }

        int w = 1;

        for (int i = pageStart; i < pageEnd; i++) {
            Kit kit = kits.get(i);

            boolean hasAccess = opener.hasPermission((doubleKit ? "pvpkit2." + kit.getName()
                    : "pvpkit1." + kit.getName()).toLowerCase()) && kit.isActived();

            String name = hasAccess ? "§a" + kit.getName() : "§c" + kit.getName();
            String kitLore = "§7" + kit.getDescription() + "\n§eClique para selecionar";
            String noAccessLore = (!kit.isActived() ? "§cEste kit está desativado\n"
                    : "§cVocê não possui este kit\n") + kitLore;

            Material iconMaterial = hasAccess ? kit.getIconMaterial() : Material.STAINED_GLASS_PANE;
            int id = hasAccess ? 0 : 14;

            menu.setItem(convertInd3x(w), new ItemBuilder(iconMaterial).setName(name)
                    .setLoreText(hasAccess ? kitLore : noAccessLore).setDurability(id)
                    .build(), (p, inv, type, stack, slot) -> {
                p.closeInventory();
                p.performCommand((doubleKit ? "kit2 " : "kit ") + kit.getName());
            });
            w += 1;
        }
        if (kits.size() == 0) {
            menu.setItem(31, new ItemBuilder(Material.BARRIER).setName(
                    "§c§lNADA AQUI").build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

                }
            });
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
