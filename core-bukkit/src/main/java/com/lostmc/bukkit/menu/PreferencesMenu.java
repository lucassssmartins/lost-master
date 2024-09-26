package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.translate.Translator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.lostmc.core.profile.data.DataType.*;

public class PreferencesMenu extends MenuInventory {

    private static Set<UUID> cooldownSet = new HashSet<>();

    static {
        Bukkit.getPluginManager().registerEvents(new PrefsCooldown(), BukkitPlugin.getInstance());
    }

    public PreferencesMenu(Profile opener) {
        super(6 * 9, Translator.tl(opener.getLocale(), "menu.preferences.title"));
        for (Preference pref : Preference.values()) {
            boolean accessDenied = pref.requiredRank == null ? false : opener.getRank().ordinal() >
                    pref.requiredRank.ordinal();
            ItemBuilder iconBuilder = new ItemBuilder(accessDenied ? Material.BOOKSHELF : pref.getMaterial());
            ItemBuilder inkBuilder = new ItemBuilder(Material.INK_SACK)
                    .setDurability(accessDenied ? 1 : byt3(opener.getData(pref.getDataType()).getAsBoolean()));
            if (accessDenied) {
                iconBuilder.setName(Translator.tl(opener.getLocale(), "menu.preferences.denied-icon-name"));
                inkBuilder.setName("§7" + ChatColor.stripColor(Translator.tl(opener.getLocale(), "menu.preferences.denied-icon-name")));
                setItem(pref.getMaterialSlot(), iconBuilder.build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

                    }
                });
                setItem(pref.getInkSlot(), inkBuilder.build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

                    }
                });
            } else {
                String nameId = "menu.preferences.icon-" + pref.getDataType().toString().toLowerCase() + "-name" +
                        (opener.getData(pref.getDataType()).getAsBoolean() ? "-true" : "-false");
                iconBuilder.setName(Translator.tl(opener.getLocale(), nameId)).setLoreText(Translator.tl(opener.getLocale(),
                        "menu.preferences.icon-" + pref.getDataType().toString().toLowerCase() + "-lore"));
                inkBuilder.setName(Translator.tl(opener.getLocale(), nameId)).setLoreText(Translator
                        .tl(opener.getLocale(), "menu.preferences.ink-" + pref.getDataType().toString().toLowerCase() + "-lore"
                                + (!opener.getData(pref.dataType).getAsBoolean() ? "-toggleon" : "-toggleoff")));
                MenuClickHandler clickHandler = new MenuClickHandler() {

                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        Profile profile = Profile.getProfile(p);
                        if (cooldownSet.contains(p.getUniqueId())) {
                            p.sendMessage(Translator.tl(profile.getLocale(), "menu.preferences.in-cooldown"));
                        } else {
                            cooldownSet.add(p.getUniqueId());
                            profile.setData(pref.getDataType(), !profile.getData(pref.getDataType()).getAsBoolean());
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), pref.getDataType(), profile.getData(pref.getDataType()))));
                            String nameId = "menu.preferences.icon-" + pref.getDataType().toString().toLowerCase() + "-name" +
                                    (opener.getData(pref.getDataType()).getAsBoolean() ? "-true" : "-false");
                            iconBuilder.setName(Translator.tl(opener.getLocale(), nameId)).setLoreText(Translator.tl(opener.getLocale(),
                                    "menu.preferences.icon-" + pref.getDataType().toString().toLowerCase() + "-lore"));
                            inkBuilder.setName(Translator.tl(opener.getLocale(), nameId)).setLoreText(Translator
                                    .tl(opener.getLocale(), "menu.preferences.ink-" + pref.getDataType().toString().toLowerCase() + "-lore"
                                            + (!profile.getData(pref.dataType).getAsBoolean() ? "-toggleon" : "-toggleoff")))
                                    .setDurability(byt3(opener.getData(pref.getDataType()).getAsBoolean()));
                            setItem(pref.getMaterialSlot(), iconBuilder.build(), this);
                            setItem(pref.getInkSlot(), inkBuilder.build(), this);
                        }
                    }
                };
                setItem(pref.getMaterialSlot(), iconBuilder.build(), clickHandler);
                setItem(pref.getInkSlot(), inkBuilder.build(), clickHandler);
            }
        }
    }

    private byte byt3(boolean b) {
        if (b)
            return 10;
        return 8;
    }

    @Getter
    public enum Preference {

        PREF_PUBLIC_STATISTICS(PUBLIC_STATISTICS, Material.HOPPER, 10, 19),
        PREF_IGNORE_ALL(CHAT, Material.BOOK_AND_QUILL, 12, 21),
        PREF_PRIVATE_MESSAGES(PRIVATE_MESSAGES, Material.SIGN, 14, 23),
        PREF_PARTY_INVITES(PARTY_INVITES, Material.BOOK, 16, 25),

        PREF_CLAN_INVITES(CLAN_INVITES, Material.ITEM_FRAME, 29, 38),
        PREF_FRIEND_INVITES(FRIEND_INVITES, Material.PUMPKIN, 31, 40),
        PREF_AUTO_JOIN_VANISHED(AUTO_JOIN_VANISHED, Material.IRON_FENCE, 33, 42, Rank.TRIAL);

        private DataType dataType;
        private Material material;
        private int materialSlot;
        private int inkSlot;
        private Rank requiredRank;

        private Preference(DataType dataType, Material material, int materialSlot, int inkSlot, Rank requiredRank) {
            this.dataType = dataType;
            this.material = material;
            this.materialSlot = materialSlot;
            this.inkSlot = inkSlot;
            this.requiredRank = requiredRank;
        }

        private Preference(DataType dataType, Material material, int materialSlot, int inkSlot) {
            this(dataType, material, materialSlot, inkSlot, null);
        }
    }

    private static class PrefsCooldown implements Listener {

        @EventHandler
        public void timer(ServerTimerEvent event) {
            if (event.getCurrentTick() % 30 != 0)
                return;
            cooldownSet.clear();
        }
    }
}
