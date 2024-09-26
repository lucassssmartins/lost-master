package com.lostmc.lobby.menu.collectables;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.craft.PluginHologramManager;
import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.internal.NPCList;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.collect.Collectable;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.translate.Translator;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.gamer.Gamer;
import com.lostmc.lobby.collectables.gadget.FireworkGadget;
import com.lostmc.lobby.collectables.gadget.Gadget;
import com.lostmc.lobby.collectables.gadget.TrampolineGadget;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CollectablesGadgetMenu extends MenuInventory {

   private ItemStack trampolineItem = new ItemBuilder(Material.WOOL)
                                    .setDurability(11).setName("§aTrampoline").build(new InteractHandler() {
        @Override
        public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
            Profile profile = Profile.getProfile(player);
            Gamer gamer = profile.getResource(Gamer.class);

            if (clicked == null || clicked.getType() == Material.AIR || clicked.isLiquid()) {
                player.sendMessage(Translator.tl(profile.getLocale(), "hub.gadget-trampoline.not-solid-block"));
                return true;
            }

            for (Hologram hologram : PluginHologramManager.getHolograms()) {
                if (clicked.getLocation().distance(hologram.getLocation()) <= 7) {
                    player.sendMessage("§cVocê não pode colocar seu trampolim aqui.");
                    return true;
                }
            }

            for (NPC npc : NPCList.getAllNPCs()) {
                if (npc.getLocation() != null &&
                        clicked.getLocation().distance(npc.getLocation()) <= 7) {
                    player.sendMessage("§cVocê não pode colocar seu trampolim aqui.");
                    return true;
                }
            }

            if (!TrampolineGadget.checkRequirements(player, clicked.getLocation())) {
                return true;
            }

            player.getInventory().remove(item);
            player.updateInventory();

            new TrampolineGadget(player).spawn(clicked.getLocation().add(0.0, 1.0, 0.0));
            return true;
        }
    });

    public CollectablesGadgetMenu(Player p, Profile profile) {
        super(4 * 9, Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.title"));

        boolean b = profile.getCollectables()
                .contains(Collectable.DEFAULT_TRAMPOLINE) ||
                profile.getRank().ordinal() <= Collectable.DEFAULT_TRAMPOLINE.getRequiredRank().ordinal() ||
                p.hasPermission(Collectable.DEFAULT_TRAMPOLINE.getPermission());
        setItem(10,
                new ItemBuilder(Material.INK_SACK).setDurability(getByt3(b))
                        .setName(Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.default-trampoline"))
                        .setLoreText(Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.default-trampoline.description")
                                + "\n\n" +
                                Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.category",
                                        Translator.tl(profile.getLocale(), "achievement.category." +
                                                Collectable.DEFAULT_TRAMPOLINE.getRarity().toString().toLowerCase()))
                                + "\n\n" +
                                Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.requirements",
                                        Tag.fromRank(Collectable.DEFAULT_TRAMPOLINE.getRequiredRank()),
                                        Collectable.DEFAULT_TRAMPOLINE.getPermission()))
                        .build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        Profile profile = Profile.getProfile(p);
                        if (profile.getCollectables().contains(Collectable.DEFAULT_TRAMPOLINE)
                                || profile.getRank().ordinal() <= Collectable.DEFAULT_TRAMPOLINE.getRequiredRank().ordinal() ||
                                p.hasPermission(Collectable.DEFAULT_TRAMPOLINE.getPermission())) {
                            Gamer gamer = profile.getResource(Gamer.class);
                            Gadget gadget = gamer.getGadget(TrampolineGadget.class);
                            if (gadget == null) {
                                if (p.getInventory().contains(trampolineItem.getType()))
                                    p.getInventory().remove(trampolineItem.getType());
                                else
                                    p.getInventory().addItem(trampolineItem);
                                p.updateInventory();
                            } else {
                                gadget.remove();
                                p.closeInventory();
                                p.sendMessage(Translator.tl(profile.getLocale(), "hub.trampoline-gadget.removed"));
                            }
                        } else {
                            p.sendMessage(Translator.tl(profile.getLocale(), "hub.collect-unavailable"));
                        }
                    }
                });
        b = profile.getCollectables()
                .contains(Collectable.FIREWORK) ||
                profile.getRank().ordinal() <= Collectable.FIREWORK.getRequiredRank().ordinal() ||
                p.hasPermission(Collectable.FIREWORK.getPermission());
        setItem(11,
                new ItemBuilder(Material.INK_SACK).setDurability(getByt3(b))
                        .setName(Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.firework"))
                        .setLoreText(Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.firework.description")
                                + "\n\n" +
                                Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.category",
                                        Translator.tl(profile.getLocale(), "achievement.category." +
                                                Collectable.FIREWORK.getRarity().toString().toLowerCase()))
                                + "\n\n" +
                                Translator.tl(profile.getLocale(), "hub.collect-gadget-menu.requirements",
                                        Tag.fromRank(Collectable.FIREWORK.getRequiredRank()),
                                        Collectable.FIREWORK.getPermission()))
                        .build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        Profile profile = Profile.getProfile(p);
                        if (profile.getCollectables().contains(Collectable.FIREWORK)
                                || profile.getRank().ordinal() <= Collectable.FIREWORK.getRequiredRank().ordinal() ||
                                p.hasPermission(Collectable.FIREWORK.getPermission())) {
                            p.closeInventory();
                            Gamer gamer = profile.getResource(Gamer.class);
                            Gadget gadget = gamer.getGadget(FireworkGadget.class);
                            if (gadget == null) {
                                p.closeInventory();
                                new FireworkGadget(p).spawn(null);
                            } else {
                                gadget.remove();
                                p.closeInventory();
                                p.sendMessage(Translator.tl(profile.getLocale(), "hub.firework-gadget.removed"));
                            }
                        } else {
                            p.sendMessage(Translator.tl(profile.getLocale(), "hub.collect-unavailable"));
                        }
                    }
                });
        setItem(31, new ItemBuilder(Material.ARROW).setName(Translator.tl(profile.getLocale(),
                "hub.collect-gen-menu.back")).build(), new MenuClickHandler() {

            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                p.openInventory(new CollectablesGenMenu(Profile.getProfile(p)));
            }
        });
    }

    private int getByt3(boolean b) {
        if (b)
            return 10;
        return 8;
    }
}
