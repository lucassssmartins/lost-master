package com.lostmc.lobby.menu.lobbies;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.menu.MenuUpdateHandler;
import com.lostmc.lobby.menu.MenuUpdaterListener;
import com.lostmc.lobby.server.ProxiedServerController;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.lostmc.core.translate.Translator.tl;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LobbiesMenu extends MenuInventory {

    public LobbiesMenu(Player p, Locale locale) {
        super(3 * 9, tl(locale, "menu.lobbies.title"));
        update(locale, Commons.getProxyHandler().getLocal().getServerType(), this);
        MenuUpdaterListener.addUpdateHandler(p, new MenuUpdateHandler(this) {
            @Override
            public void onUpdate(Player p) {
                update(locale, Commons.getProxyHandler().getLocal().getServerType(), this.menu);
            }
        });
    }

    private void update(Locale locale, ServerType serverType, MenuInventory menu) {
        menu.clear();
        ProxiedServerController controller = Lobby.getControl().getController(ProxiedServerController.class);
        List<ProxiedServer> lobbies = controller.getAvailableServers(serverType);
        lobbies.sort(Comparator.comparing(ProxiedServer::getId));
        for (int i = 0; i < lobbies.size(); i++) {
            ProxiedServer server = lobbies.get(i);
            boolean conn3cted = Commons.getProxyHandler().getLocal().getId().equals(server.getId());
            setItem(conv3rtIndex(i),
                    new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(conn3cted ? 5 : 8)
                    .setName(tl(locale, "menu.lobbies.lobbie-"
                            + server.getServerType().toString().toLowerCase() + "-item-name",
                            server.getId().replaceAll("[a-zA-Z0-]", "")))
                    .setLoreText(tl(locale, "menu.lobbies.lobbie-" + server.getServerType().toString().toLowerCase()
                            + "-item-lore", server.getPlayers().size(), server.getMaxPlayers())).build(),
                    (p, inv, type, stack, slot) -> {
                                ByteArrayDataOutput data = ByteStreams.newDataOutput();
                                data.writeUTF("Connect");
                                data.writeUTF(server.getId());
                                p.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                            });
        }
    }

    public int conv3rtIndex(int slot) {
        return (slot + 10);
    }
}
