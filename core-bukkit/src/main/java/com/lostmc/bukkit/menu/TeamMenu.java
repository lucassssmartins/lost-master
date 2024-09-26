package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.property.IProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamMenu extends MenuInventory {

    private static int itemsPerPage = 28;

    public TeamMenu(List<Map.Entry<Profile, IProperty>> teamList) {
        super(6 * 9, "§8Equipe");
        buildItems(this, 1, teamList);
    }

    public void buildItems(MenuInventory menu, int page, List<Map.Entry<Profile, IProperty>> teamList) {
        menu.clear();

        List<Map.Entry<Profile, IProperty>> ranks = teamList;
        int pageStart = 0;
        int pageEnd = itemsPerPage;
        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }
        if (pageEnd > ranks.size()) {
            pageEnd = ranks.size();
        }
        if (page != 1) {
            menu.setItem(0, new ItemBuilder(Material.ARROW).setName("§aPágina anterior").build(),
                    new MenuClickHandler() {

                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, (page - 1), teamList);
                        }
                    });
        }
        if (Math.ceil(ranks.size() / itemsPerPage) + 1 > page) {
            menu.setItem(8, new ItemBuilder(Material.ARROW).setName("§aPróxima página").build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, (page + 1), teamList);
                        }
                    });
        }

        int w = 1;

        for (int i = pageStart; i < pageEnd; i++) {
            Map.Entry<Profile, IProperty> teamEntry = teamList.get(i);

            Profile teamMember = teamEntry.getKey();
            IProperty iproperty = teamEntry.getValue();

            Tag mainTag = Tag.fromRank(teamMember.getRank());

            String name = "§" + mainTag.getColorId() + teamMember.getName();
            String lore = "§7Cargo: " + mainTag.getColouredName(true).toUpperCase();

            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            if (iproperty != null) {
                profile.getProperties().put("textures", new Property("textures", iproperty.getValue(), iproperty.getSignature()));
            }

            menu.setItem(convertInd3x(w), new ItemBuilder(Material.SKULL_ITEM).setName(name)
                            .setPlayerHead(profile).setLoreText(lore).setDurability(3)
                    .build(), (p, inv, type, stack, slot) -> {
            });
            w += 1;
        }
        if (ranks.size() == 0) {
            menu.setItem(31, new ItemBuilder(Material.BARRIER).setName("§cNADA AQUI")
                    .build(), new MenuClickHandler() {
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
