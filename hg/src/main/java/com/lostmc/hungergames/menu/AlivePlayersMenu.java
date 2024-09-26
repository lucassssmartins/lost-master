package com.lostmc.hungergames.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.manager.KitManager;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlivePlayersMenu extends MenuInventory {

    private static int itemsPerPage = 28;

    public AlivePlayersMenu() {
        super(6 * 9, "§8Lista de jogadores");
        buildItems(this, 1);
    }

    public void buildItems(MenuInventory menu, int page) {
        menu.clear();

        GamerManager manager = Management.getManagement(GamerManager.class);
        List<HungerGamer> gamers = new ArrayList<>(manager.getAliveGamers());

        int pageStart = 0;
        int pageEnd = itemsPerPage;
        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }
        if (pageEnd > gamers.size()) {
            pageEnd = gamers.size();
        }
        if (page != 1) {
            menu.setItem(0, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName("§aPágina anterior").build(),
                    new MenuClickHandler() {

                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, (page - 1));
                        }
                    });
        }
        if (Math.ceil(gamers.size() / itemsPerPage) + 1 > page) {
            menu.setItem(8, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName("§aPróxima página").build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, (page + 1));
                        }
                    });
        }

        int w = 1;

        for (int i = pageStart; i < pageEnd; i++) {
            HungerGamer gamer = gamers.get(i);

            String playerName = "...";
            GameProfile profile;
            Player target = Bukkit.getPlayer(gamer.getUniqueId());
            if (target != null) {
                playerName = target.getName();
                profile = ((CraftPlayer) target).getHandle().getProfile();
            } else {
                profile = new GameProfile(UUID.randomUUID(), null);
            }

            StringBuilder lore = new StringBuilder();

            lore.append("§eKit: §6" + gamer.getKits().values());
            lore.append("\n§eKills: §6" + gamer.getMatchKills());

            menu.setItem(convertInd3x(w), new ItemBuilder(Material.SKULL_ITEM).setName("§a" + playerName)
                    .setPlayerHead(profile).setDurability(3).setLoreText(lore.toString()).build(),
                    (p, inv, type, stack, slot) -> {
                if (target != null && target.isOnline()) {
                    p.teleport(target);
                }
            });
            w += 1;
        }
        if (gamers.size() == 0) {
            menu.setItem(31, new ItemBuilder(Material.BARRIER).setName(
                    "§cNenhum jogador jogando").build(), new MenuClickHandler() {
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
