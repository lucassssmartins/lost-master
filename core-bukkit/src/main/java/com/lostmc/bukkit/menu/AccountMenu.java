package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.property.IProperty;
import com.lostmc.core.property.IPropertyGetter;
import com.lostmc.core.property.SkinSource;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
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

public class AccountMenu extends MenuInventory {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public AccountMenu(Profile target, Profile opener, boolean readyOnly, boolean admin) {
        super(3 * 9, tl(opener.getLocale(), "menu.account.title", target.getName()));

        Rank generalRank = target.getRank();
        int ranksSize = target.getRanks().size();
        String locale = target.getLocale().toString().toUpperCase().replace("_", "-");
        String firstLoggedIn = formatter.format(new Date(target.getData(DataType.FIRST_LOGGED_IN).getAsLong()));
        String lastLoggedIn = formatter.format(new Date(target.getData(DataType.FIRST_LOGGED_IN).getAsLong()));

        String accountLore = readyOnly ? admin ?
                // ADMIN
                tl(opener.getLocale(), "menu.account.full-details-lore",
                        Tag.fromRank(generalRank).getColouredName(false), ranksSize,
                        locale, firstLoggedIn, lastLoggedIn) :
                // READY ONLY
                tl(opener.getLocale(), "menu.account.ready-only-lore") :
                // PERSONAL
                tl(opener.getLocale(), "menu.account.full-details-lore",
                        Tag.fromRank(generalRank).getColouredName(false), ranksSize,
                        locale, firstLoggedIn, lastLoggedIn);

        ItemBuilder accountItem = new ItemBuilder(Material.SKULL_ITEM).setDurability(3)
                .setPlayerHead(provideSkinProfile(target, target.getUniqueId())).setName("§a" + target.getName())
                .setLoreText(accountLore);
        setItem(10, accountItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                if (readyOnly && !admin)
                    return;
                p.openInventory(new AccountRanksMenu(target, Profile.getProfile(p)));
            }
        });

        boolean publicStats = readyOnly ? admin ? true : target.getData(DataType.PUBLIC_STATISTICS).getAsBoolean() : true;
        ItemBuilder statsItem = new ItemBuilder(Material.PAPER).setName(tl(opener.getLocale(),
                publicStats ? "menu.account.stats-for-enabled" : "menu.account.stats-for-disabled"))
                .setLoreText(tl(opener.getLocale(), publicStats ? "menu.account.stats-enabled-for" :
                        "menu.account.stats-disabled-for"));
        setItem(11, statsItem.build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
;                if (!publicStats) {
                    p.sendMessage(tl(Profile.getProfile(p).getLocale(), "menu.account.stats-unavailable-for"));
                } else {

                }
            }
        });


    }

    private GameProfile provideSkinProfile(Profile profile, UUID id) {
        Player target = Bukkit.getPlayer(id);
        if (target != null) {
            GameProfile provided = new GameProfile(UUID.randomUUID(), null);
            try {
                provided.getProperties().put("textures",
                        ((CraftPlayer) target).getHandle().getProfile().getProperties().get("textures").iterator().next());
            } catch (Exception noPropertyFound) {
            }
            return provided;
        } else {
            GameProfile fetched = new GameProfile(UUID.randomUUID(), null);
            if (profile.getSkinSource() == null || profile.getSkinSource() == SkinSource.ACCOUNT) {
                IProperty iproperty = IPropertyGetter.getProperty(id.toString());
                if (iproperty != null) {
                    fetched.getProperties().put("textures",
                            new Property("textures", iproperty.getValue(), iproperty.getSignature()));
                }
            } else {
                IProperty iproperty = profile.getProperty();
                if (iproperty != null) {
                    fetched.getProperties().put("textures",
                            new Property("textures", iproperty.getValue(), iproperty.getSignature()));
                }
            }
            return fetched;
        }
    }
}
