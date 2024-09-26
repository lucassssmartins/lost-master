package com.lostmc.lobby.menu.games;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.server.minigame.HungerGamesServer;
import com.lostmc.core.server.minigame.MinigameState;
import com.lostmc.core.utils.DateUtils;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.menu.MenuUpdateHandler;
import com.lostmc.lobby.menu.MenuUpdaterListener;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HGMixMenu extends MenuInventory {

    private static int maxMenuItems = 5;
    private static HungerGamesServer hg = null;

    public HGMixMenu(Player p) {
        super(4 * 9, "§8Servidores de HG");
        update(this);
        MenuUpdaterListener.addUpdateHandler(p, new MenuUpdateHandler(this) {
            @Override
            public void onUpdate(Player p) {
                update(menu);
            }
        });
    }

    private void update(MenuInventory menu) {
        menu.clear();

        ProxiedServerController manager = Lobby.getControl().getController(ProxiedServerController.class);

        List<ProxiedServer> servers = new ArrayList<>(manager.getCachedServers());
        servers.removeIf(server -> server.getServerType() != ServerType.HUNGERGAMES);
        servers.sort(Comparator.comparing(ProxiedServer::getId));

        int next = 1;
        for (ProxiedServer server : servers) {
            HungerGamesServer hg = (HungerGamesServer) server;
            ItemBuilder builder;
            int amount = hg.getPlayers().size();
            if (amount == 0)
                amount = 1;
            if (hg.getState() == MinigameState.WAITING) {
                builder = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§aHG #" + hg
                        .getId().replaceAll("[a-zA-Z0-]", "")).setLoreArray("§aPartida aguardando jogadores...",
                        "§fJogadores: §a" + hg.getPlayers().size() + "/" + hg.getMaxPlayers(),
                        "§fModo: §a" + (hg.isDoubleKit() ? "Double Kit" : "Single Kit")).setDurability(5);
            } else if (hg.getState() == MinigameState.PREGAME || hg.getState() == MinigameState.STARTING) {
                builder = new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§aHG #" + hg
                                .getId().replaceAll("[a-zA-Z0-]", ""))
                        .setLoreArray("§aPartida iniciando em " + DateUtils.formatDifference(hg.getTime()),
                                "§fJogadores: §a" + hg.getPlayers().size() + "/" + hg.getMaxPlayers(),
                                "§fModo: §a" + (hg.isDoubleKit() ? "Double Kit" : "Single Kit")).setDurability(5);
            } else {
                builder = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(14)
                        .setName("§cHG #" + hg.getId().replaceAll("[a-zA-Z0-]", ""));
                if (hg.getState() == MinigameState.INVINCIBILITY) {
                    builder.setLoreArray("§cInvencibilidade acaba em "
                            + DateUtils.formatDifference(hg.getTime()), "§fJogadores: §a"
                            + hg.getPlayers().size() + "/" + hg.getMaxPlayers(), "§fModo: §a"
                            + (hg.isDoubleKit() ? "Double Kit" : "Single Kit"));
                } else if (hg.getState() == MinigameState.GAMETIME) {
                    builder.setLoreArray("§cTempo de jogo: "
                            + DateUtils.formatDifference(hg.getTime()), "§fJogadores: §a"
                            + hg.getPlayers().size() + "/" + hg.getMaxPlayers(), "§fModo: §a"
                            + (hg.isDoubleKit() ? "Double Kit" : "Single Kit"));
                } else if (hg.getState() == MinigameState.ENDING) {
                    builder.setLoreArray("§cPartida finalizando!", "§fJogadores: §a"
                            + hg.getPlayers().size() + "/" + hg.getMaxPlayers(), "§fModo: §a"
                            + (hg.isDoubleKit() ? "Double Kit" : "Single Kit"));
                } else {
                    builder.setLoreArray("§cFalha ao carregar status do servidor.");
                }
            }
            setItem(convertInd3x(next), builder.setAmount(amount).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    ByteArrayDataOutput data = ByteStreams.newDataOutput();
                    data.writeUTF("Connect");
                    data.writeUTF(hg.getId());
                    p.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                }
            });
            ++next;
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
