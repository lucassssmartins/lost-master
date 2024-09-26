package com.lostmc.lobby.menu.games;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.menu.MenuUpdateHandler;
import com.lostmc.lobby.menu.MenuUpdaterListener;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.lostmc.core.translate.Translator.tl;

public class GameSelectorMenu extends MenuInventory {

    public GameSelectorMenu(Player p, Profile profile) {
        super(3 * 9, tl(profile.getLocale(), "menu.game-selector.title"));
        update(profile.getLocale(), this);
        MenuUpdaterListener.addUpdateHandler(p, new MenuUpdateHandler(this) {
            @Override
            public void onUpdate(Player p) {
                update(Profile.getProfile(p).getLocale(), menu);
            }
        });
    }

    private void update(Locale locale, MenuInventory menu) {
        ProxiedServerController controller = Lobby.getControl().getController(ProxiedServerController.class);

        menu.setItem(10, new ItemBuilder(Material.IRON_CHESTPLATE).setName(
                        tl(locale, "menu.game-selector.pvp-item-name"))
                .setLoreText(tl(locale, "menu.game-selector.pvp-item-lore",
                        controller.getOnlineCount(ServerType.PVP))).build(), new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                ProxiedServer server = Lobby.getControl().getController(ProxiedServerController.class)
                        .getMostConnection(ServerType.PVP);
                if (server != null) {
                    ByteArrayDataOutput data = ByteStreams.newDataOutput();
                    data.writeUTF("Connect");
                    data.writeUTF(server.getId());
                    p.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                } else {
                    p.sendMessage("§cNenhum servidor de pvp encontrado.");
                }
            }
        });

        menu.setItem(11, new ItemBuilder(Material.MUSHROOM_SOUP).setName(
                        tl(locale, "menu.game-selector.hg-item-name"))
                .setLoreText(tl(locale, "menu.game-selector.hg-item-lore",
                        (controller.getOnlineCount(ServerType.HUNGERGAMES)
                                + controller.getOnlineCount(ServerType.HG_LOBBY)))).build(),
                new MenuClickHandler() {
            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                ProxiedServer server = Lobby.getControl().getController(ProxiedServerController.class)
                        .getMostConnection(ServerType.HG_LOBBY);
                if (server != null) {
                    ByteArrayDataOutput data = ByteStreams.newDataOutput();
                    data.writeUTF("Connect");
                    data.writeUTF(server.getId());
                    p.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                } else {
                    p.sendMessage("§cNenhum servidor de hg_lobby encontrado.");
                }
            }
        });
    }
}
