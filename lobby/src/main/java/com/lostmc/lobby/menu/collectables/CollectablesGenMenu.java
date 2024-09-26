package com.lostmc.lobby.menu.collectables;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.collect.Collectable;
import com.lostmc.core.profile.collect.CollectCategory;
import com.lostmc.core.profile.collect.CollectRarity;
import com.lostmc.core.translate.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CollectablesGenMenu extends MenuInventory {

    public CollectablesGenMenu(Profile profile) {
        super(3 * 9, Translator.tl(profile.getLocale(), "hub.collect-gen-menu.title"));
        setItem(10, new ItemBuilder(Material.PISTON_BASE)
                .setName(Translator.tl(profile.getLocale(), "hub.collect-gen-menu.gadget-item-name"))
                .setLoreText(Translator.tl(profile.getLocale(), "hub.collect-gen-menu.gadget-item-lore")
                        + "\n\n"
                        + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.unlocked-gadgets",
                        profile.getCollectables(CollectCategory.GADGET).size(),
                        Collectable.getByCategory(CollectCategory.GADGET).size())
                        + "\n"
                        + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.commons-gadget",
                        profile.getCollectables(CollectCategory.GADGET, CollectRarity.COMMON).size(),
                        Collectable.getByCategoryAndRarity(CollectCategory.GADGET, CollectRarity.COMMON).size())
                        + "\n"
                        + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.rares-gadget",
                        profile.getCollectables(CollectCategory.GADGET, CollectRarity.RARE).size(),
                        Collectable.getByCategoryAndRarity(CollectCategory.GADGET, CollectRarity.RARE).size())
                        + "\n"
                        + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.epics-gadget",
                        profile.getCollectables(CollectCategory.GADGET, CollectRarity.EPIC).size(),
                        Collectable.getByCategoryAndRarity(CollectCategory.GADGET, CollectRarity.EPIC).size())
                        + "\n"
                        + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.legendary-gadget",
                        profile.getCollectables(CollectCategory.GADGET, CollectRarity.LEGENDARY).size(),
                        Collectable.getByCategoryAndRarity(CollectCategory.GADGET, CollectRarity.LEGENDARY).size())
                        + "\n\n" + Translator.tl(profile.getLocale(), "hub.collect-gen-menu.gadget-click-to-browse"))
                .build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                p.openInventory(new CollectablesGadgetMenu(p, Profile.getProfile(p)));
            }
        });
    }
}
