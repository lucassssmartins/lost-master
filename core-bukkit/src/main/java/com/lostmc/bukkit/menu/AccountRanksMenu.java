package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.lostmc.core.translate.Translator.tl;

public class AccountRanksMenu extends MenuInventory {

    private static int itemsPerPage = 28;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public AccountRanksMenu(Profile target, Profile opener) {
        super(6 * 9, tl(opener.getLocale(), "menu.account-ranks.title", target.getName()));
        buildItems(this, 1, target, opener);
    }

    public void buildItems(MenuInventory menu, int page, Profile target, Profile opener) {
        menu.clear();

        List<RankProduct> ranks = target.getRanks();
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
            menu.setItem(0, new ItemBuilder(Material.INK_SACK).setDurability(10)
                            .setName("§a§l<< Página anterior").build(),
                    new MenuClickHandler() {

                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            buildItems((MenuInventory) inv, (page - 1), target, Profile.getProfile(p));
                        }
                    });
        }
        if (Math.ceil(ranks.size() / itemsPerPage) + 1 > page) {
            menu.setItem(8, new ItemBuilder(Material.INK_SACK).setDurability(10).setName("§a§lPróxima página >>").build(),
                    new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    buildItems((MenuInventory) inv, (page + 1), target, Profile.getProfile(p));
                }
            });
        }

        int w = 1;

        for (int i = pageStart; i < pageEnd; i++) {
            RankProduct product = ranks.get(i);

            String name = tl(opener.getLocale(), "menu.account-ranks.rank-name",
                    Tag.fromRank(product.getObject()).getColouredName(false));

            String receiveTime = formatter.format(new Date(product.getReceivedTime()));
            String isPaid = product.isPaid() ? tl(opener.getLocale(), "menu.account-ranks.paid-rank") :
                    tl(opener.getLocale(), "menu.account-ranks.non-paid-rank");
            String durability = product.isLifetime() ? tl(opener.getLocale(), "core.translation.eternal") :
                    DateUtils.getTime(product.getExpirationTime());

            String lore = tl(opener.getLocale(), "menu.account-ranks.rank-lore", i, receiveTime,
                    isPaid, durability, product.getSenderName());

            menu.setItem(convertInd3x(w), new ItemBuilder(Material.NAME_TAG).setName(name)
                    .setLoreText(lore)
                    .build(), (p, inv, type, stack, slot) -> {
            });
            w += 1;
        }
        if (ranks.size() == 0) {
            menu.setItem(31, new ItemBuilder(Material.BARRIER).setName(
                    tl(opener.getLocale(), "menu.account-ranks.no-ranks")).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

                }
            });
        }

        menu.setItem(49, new ItemBuilder(Material.ARROW).setName(tl(opener.getLocale(), "menus.back.name"))
                        .setLoreText(tl(opener.getLocale(), "menus.back.lore")).build(),
                (p13, inv1, type1, stack1, slot1)
                        -> p13.openInventory(new AccountMenu(target, Profile.getProfile(p13), !p13.getUniqueId().equals(
                        target.getUniqueId()) ? true : false, opener.getRank().ordinal() <= Rank.TRIAL.ordinal())));
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
