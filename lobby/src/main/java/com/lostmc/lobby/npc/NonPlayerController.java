package com.lostmc.lobby.npc;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.api.handler.TouchHandler;
import com.lostmc.bukkit.hologram.api.line.TextLine;
import com.lostmc.bukkit.npc.NpcController;
import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.api.skin.Skin;
import com.lostmc.bukkit.npc.api.state.NPCSlot;
import com.lostmc.bukkit.npc.internal.NPCBase;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.menu.games.HGMixMenu;
import com.lostmc.lobby.server.ProxiedServerController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NonPlayerController extends Controller implements Listener {

    private Map<NonPlayerType, NPC> nonPlayerMap = new HashMap<>();
    private Map<NonPlayerType, Hologram> hologramMap = new HashMap<>();

    public NonPlayerController(Control control) {
        super(control);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        nonPlayerMap.values().forEach(npc -> npc.show(event.getPlayer()));
    }

    @EventHandler
    public void onTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        ProxiedServerController manager = getControl().getController(ProxiedServerController.class);
        for (Map.Entry<NonPlayerType, Hologram> entry : hologramMap.entrySet()) {
            switch (entry.getKey()) {
                case PVP: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + manager
                            .getOnlineCount(ServerType.PVP) + " jogando agora");
                    break;
                }
                case HG: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + (manager
                            .getOnlineCount(ServerType.HUNGERGAMES) + manager.getOnlineCount(ServerType.HG_LOBBY))
                            + " jogando agora");
                    break;
                }
                case HGMIX: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + manager
                            .getOnlineCount(ServerType.HUNGERGAMES)
                            + " jogando agora");
                    break;
                }
                case EVENTS: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + manager
                            .getOnlineCount(ServerType.HG_EVENT)
                            + " jogando agora");
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        for (NonPlayerType nonPlayer : NonPlayerType.values()) {
            String key = "non-player." + nonPlayer.toString().toLowerCase();
            if (!getPlugin().getConfig().isSet(key))
                continue;

            Location location = Commons.getGson().fromJson(getPlugin().getConfig().getString(key),
                    ILocation.class).toLocation(Bukkit.getWorlds().get(0));

            NpcController npcManager = Lobby.getControl().getController(NpcController.class);
            NPC npc = npcManager.createNPC().setLocation(location).setItem(NPCSlot.MAINHAND, nonPlayer.itemInHand)
                    .setSkin(new Skin(nonPlayer.value, nonPlayer.signature)).create();
            getServer().getOnlinePlayers().forEach(p -> npc.show(p));

            this.nonPlayerMap.put(nonPlayer, npc);

            HologramManager manager = getControl().getController(HologramManager.class);
            Hologram hologram = manager.createHologram(getPlugin(), location.clone().add(0, 1.85, 0));
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine("§e...");
            hologram.appendTextLine(nonPlayer.title);
            if (nonPlayer.hasSpotlight()) {
                hologram.appendTextLine(nonPlayer.spotlight);
            }

            hologramMap.put(nonPlayer, hologram);

            if (nonPlayer.equals(NonPlayerType.PVP)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.PVP);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de pvp encontrado.");
                    }
                });
            } else if (nonPlayer.equals(NonPlayerType.HG)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.HG_LOBBY);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de hg_lobby encontrado.");
                    }
                });
            } else if (nonPlayer.equals(NonPlayerType.HGMIX)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    getServer().getScheduler().runTask(getPlugin(),
                            () -> player.openInventory(new HGMixMenu(player)));
                });
            } else if (nonPlayer.equals(NonPlayerType.EVENTS)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.HG_EVENT);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de evento encontrado.");
                    }
                });
            }
        }
        registerListener(this);
    }

    public void saveLocation(Location location, NonPlayerType nonPlayer) {
        NPC npc;
        if (nonPlayerMap.containsKey(nonPlayer)) {
            npc = nonPlayerMap.get(nonPlayer).setLocation(location);
            Hologram hologram = this.hologramMap.get(nonPlayer);
            Location last = location.clone().add(0, 1.85, 0);
            hologram.teleport(last);
        } else {
            NpcController npcManager = Lobby.getControl().getController(NpcController.class);
            npc = npcManager.createNPC().setLocation(location).setItem(NPCSlot.MAINHAND, nonPlayer.itemInHand)
                    .setSkin(new Skin(nonPlayer.value, nonPlayer.signature));
            npc.create();
            getServer().getOnlinePlayers().forEach(p -> npc.show(p));

            this.nonPlayerMap.put(nonPlayer, npc);

            HologramManager manager = getControl().getController(HologramManager.class);
            Hologram hologram = manager.createHologram(getPlugin(), location.clone().add(0, 1.85, 0));

            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine("§e...");
            hologram.appendTextLine(nonPlayer.title);
            if (nonPlayer.hasSpotlight()) {
                hologram.appendTextLine(nonPlayer.spotlight);
            }

            hologramMap.put(nonPlayer, hologram);

            if (nonPlayer.equals(NonPlayerType.PVP)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.PVP);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de pvp encontrado.");
                    }
                });
            } else if (nonPlayer.equals(NonPlayerType.HG)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.HG_LOBBY);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de hg_lobby encontrado.");
                    }
                });
            } else if (nonPlayer.equals(NonPlayerType.HGMIX)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ByteArrayDataOutput data = ByteStreams.newDataOutput();
                    data.writeUTF("PLAY_HGMIX");
                    player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                });
            } else if (nonPlayer.equals(NonPlayerType.EVENTS)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ProxiedServer server = getControl().getController(ProxiedServerController.class)
                            .getMostConnection(ServerType.HG_EVENT);
                    if (server != null) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF(server.getId());
                        player.sendPluginMessage(Lobby.getInstance(), "BungeeCord", data.toByteArray());
                    } else {
                        player.sendMessage("§cNenhum servidor de evento encontrado.");
                    }
                });
            }
        }

        String key = "non-player." + nonPlayer.toString().toLowerCase();

        getPlugin().getConfig().set(key, Commons.getGson().toJson(new ILocation(location)));
        getPlugin().saveConfig();
    }

    @Override
    public void onDisable() {

    }

    public enum NonPlayerType {

        // MAIN LOBBY
        HG("§5§l> §d§lDESTAQUE §5§l<", "§6HG", new ItemStack(Material.MUSHROOM_SOUP),
                "ewogICJ0aW1lc3RhbXAiIDogMTY1MTU4OTQxMTk5OSwKICAicHJvZmlsZUlkIiA6ICIxMGYwY2YzYTBhYzI0ZTUzYTliYzQ5NmNkMGFhYWE5YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGcmFhbmNpc2NvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RlODIwYTVhMDM2OTk1MzM4ZDIyYjI3ZTMwM2I4YjMwYjZlYjU1MzZkZjc4ZDRmMjE4Y2UxZDRlN2RmYTRhOSIKICAgIH0KICB9Cn0=",
                "n7IEVzcbYot+hrJdnjVAhQSf6WfFPNla5tYF9G6V2P9WN3Qc3q8NetNvOQAUNunm0/AMD390zWYWhzgpBHFCdBtRIN6e/om2/D+Frfs8kZqu+2+IV9c+L6dzNCri4p70zou8JaMp63m8ncO2dZJFvaGWiPdUATbPj4Rw7PYPr9xkKxjooosv0umdxTWgWADusKi/AlfnYEaOLtAB3BOgFUzJVsFIlVoA595Vh9DT1EmSjbFe6KXem1GMDz9DQj5KYCHUtPznScs2rs4SNlwkIBoHwjh1/68lyka9KnbwCkuneY+iQOxpnYS9kR46I++WnYaR4iYV/eCz88d2TQ/RU/PfNA01/kEN3L6CKk10CBnYbHUB3NPmwWgFHkJFK40APtycVAX7BnRstYZws/4bOT39NX+pbzezUxOWAoMFJOcQSUE/wijYkj/LnArBAtzC4v47tZdolHzW+hDUh6wx/WaMLOuHyjHG7dZM+9sdR8kmttXyC/Xb+hXp2h/WW72gkumhyE9gtpASwZ2CRNyEBmOfHa0FanQ73SzmU1Mxz0/tgzkY3pvnr3W06qvFhFHn2eKlEFi07Uw9X6sY8S0CH5wgMYhQnZoa50yQ7+vytlhIuNISLnORG6l2gEPGVU7Z/2HM8K4pO9GHCY+O/NPXGgZZ8XN4sBHH7uUhTXAknz8="),
        PVP("§5§l> §d§lSIMULATOR + 1V1 §5§l<", "§6PvP", new ItemStack(Material.IRON_CHESTPLATE),
                "ewogICJ0aW1lc3RhbXAiIDogMTY1MTU4OTg4NDIwMywKICAicHJvZmlsZUlkIiA6ICJmMTdkOWJlZmM2NGM0YzA4ODVhYWU3NWQ0YjhiNWE0NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTaXJCYW5kZXJzbmF0Y2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIyOGFjYTE5NTQ4MzNhZTYzMjkxMWQxMjkwZjFjMTJlMDIyZWZhN2Q5NGVlYmJjZmY0MmYxN2ExNTM4NjJlOSIKICAgIH0KICB9Cn0=",
                "Hh3NVhAM/otsNoTZf2TpSLOdAUUBJ93hhWKdJ8Sv3CWqxfVxFKucnwdgykSVqiy+j+z+hNzFZhEd0L7nPdMabZB6J58PWht70ce/1oKAJJGaWFz+tIfnrbYSA5NwpBj/9aDmFbdtih/EiZYLyzH6+4UFSZLpOlgjrBd4dtPBll6SUQk9Z1X2JYL2jNNyutWR3KR5o78jmAD0ys39favfiZ4inrEnDOIgpNlYSrHBLvUUgbNZTCcWJ2Z0OptexRQ4UQlQCjbCAHGYscUQSj25RGQ9+qGKqFtaf2L5yIeLZNkewGlf9k9MUh4ylyNAPU5cV9uf1DCXGJg1BxNEdyyTaN8Vq1FxFYAPQJ9Ibte3d3ZwJtPX1ohJkU0BwOgrcW6NjsZBliUt47vB3PlRannIYQBagm2zBrO2tZsSiAYTtVA3aARw3ZhsONaXRI5o7dADtbmibyX9qu2AfzK1XqNQQOquiTl96o9PQzOUZmORvJcWf8WXcBvvfq8xRBHs702jGA9qxPeO8FrOP1nQzq6yYFkbXhcmgdk1FWKj3xtOo5Jx+qAbMiu1hNB9NkJL8vmmu5bkCnNyluELqiDAPraPX99FzeQMucb3oWlC3Xc/ixkwJDbCpajYvbrWZ9RKQ7Ics0lh1QNLdCzBi5xGG/zuggLTuwCwhDIx9JlFMHHI9DY="),

        //HG LOBBY
        CXC("§d§lEM BREVE", "§6Clan X Clan", new ItemStack(Material.IRON_CHESTPLATE),
                "ewogICJ0aW1lc3RhbXAiIDogMTY1NTU0NTEzODUwMiwKICAicHJvZmlsZUlkIiA6ICJmNjI0NTQ0MTQ4ODI0NDY2OWRhMTE4ODNkZTU2NTg3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHYWJpZGVkIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU0ZmU1ZWI3ZmRjZGU0ODRiODEwYTQwNGU0NDcwMmJkNTI2ZmJhYjViZmYyMTg4NDIxOTJkMzc5M2M4ZmQ0ZWQiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9",
                "Ty4/FZgPNljzDYYk3vCOmrnLtwM30HEySY998Wm5X3MZx/6EHVDVn2GobCtbRChQEwWVe1itTLgmy2/7usJKPDved9fGbeOt6Vl7WMEwTZYeIo3UWc+cLc9fz2Of9gfwBnfmuPH5UHKT6176EMXRwExUxVEJd9bzw9Cd6JjQb7NKE5OkiXjDKtK8C/20jr1V4kVpQnMAWVkJ57V5be411l/ka0dy+v4DX+lTlwIkhvC6n7LwETt9Mk4xk85mLkzUPsEgpSz3jndzEDIFuIcXo3xY7jl+p+5EnQcdkUcbUjAeQaa1mkvVOr4rdafPwoJWu++vlOkK+14wcqliMr6ofB1ZAIZhXyS1NGItfJSde3JBkrdqDXdJnZrJoJjuAzRi0pJ/YV2Hxx8TDwPQPQhRyeOeFPS2bC3tP+Wnvb+LqgLMBnXwi/qGsbTIQbAyjO5X1T3juY9cDURKCsfzUDjlS5jDbi7PmoqikLsav/HbNRoM+2P04VQKUAEAmVjZ+MxjNy0KjRvoO90KVDPou58gfAGcAbw1ylByeM5HXagZabdz+TaYHDGqihxaUq1HfRcfVhcZQKgv2ZKiAGq1cI527mFQQ1S7X8wKvlG7cjO21lHXGB0L1sq6pXubM2OTLyLhMLe0/O2v2z9+GMJ7Yvm0tQQCsfLUlpY1BkLu/sD0EKw="),
        EVENTS("", "§6Eventos", new ItemStack(Material.IRON_SWORD),
                "eyJ0aW1lc3RhbXAiOjE1MTkwNTU3NzEyOTIsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mMDhiMjBiOTU2YTAzODk3YjA0NGY0Y2NiMzcyZGJhOTM5Y2U5NTlmNjk5OWMyZDEwMjJmYmJhZjA0N2MzIn19fQ==",
                "O3Wvzb4g0+dEBXuTUYrHJW37/Ltn46Ne3Vm2Q9q/902YqJSL/sia9XWzpqsId4jKeT2biesBRhpKIX0xX2XEdHIK0Mt0EQD12B98qrfAIcSyCrPU+7wTVr4HXBNiXmaJSv8ZrpNBMO8tbYAV3Y9vOF+VqV63vBNwp/I9b2fpP5kLDQlxlMmGX+UUKgmoChTmjRIQ6uBLxFN0pak8r3rKPzCUdYRWPj8hpZROxgQQBBQXESW+wvcWgGr0Le6HD3Sb+ddTHTuudvQVPemJJbJTDeidpfo4nzzBxkIbh+/wOOd/0wXB6iUo8qKZx7efAJ/GaCcByIWyavy2CpxEdzgQQqD5vy2dsgSbOQIrmjQWNd6ebTWIvf3uxC1+1Us2uutINSLz5Mjm/YyvJTuhOT7yDsdX4EvTYl5RD19SOdD3JHJipT+dKV6go1ctjWC+lOg4PzMronG4VN5vgjMdqf1m/SxfyEgZ9YLkHXvRx+Z9ElU+qlSVKMHNpWkB9bPTkjLzTTBkhiZ6ENoQQ9zCR/RAkR3qIRvwL6EvwC4+aCcUA+Q0TZLFndmy1jrmrmcA7K6syGIV1yaEnv0PZ6RHpQqTq+dWMaBS6K5NQe3zFb9fHTg5epNV9wUWXI9Aa+ofVUPP4RN3Qz2H9ed7h3NrAWuKpT2Tfcx6NEWGZ5VomHaoC2g="),
        CHAMPIONS("§d§l15h 17h 19h 21h", "§6Champions League", new ItemStack(Material.DIAMOND_SWORD),
                "ewogICJ0aW1lc3RhbXAiIDogMTY1NTU0NDgyMjM5MSwKICAicHJvZmlsZUlkIiA6ICIwYjExZjVlZGRlN2Y0NjQ5OTM4ODQzYjc0MDRkYTljNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaGF3MDA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EzNzRiYTdmN2UyYmNjMDNmNzFlZTQ4OTcwZmY1OGEzNzYyMDVmMzgxYmFhN2M4ODI2ZTE4YmZkOGIyMTM5YjIiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9",
                "ZriwJRQ0l7jrt4zaNBjdThE/vUFUkUsMGMcBNQzbNBowPe/wK0x8NamdCydn5wEe7xjIP1OyhgDFyauCG1ZpbPFT0mgvPqQ/8WsqWW7Pzl2gY6AL5jofBKsNXrNgIFi7ul/7XIjLAgVBagaA//u2gx1Ty6am4UPtqZ67mZ02fnCUawE5AYzFAXu/gG467ITvXHxnyQxEBfDBcYO1greoZ/zRBIwl98AWXSDTftQMaOVWagmMtNNLqkD313o5OsezSqOUgOpsAuL2ir3prk3xrgBwYclSvnhSRs3CVYva1Uuhi5tB4qtlhUBfkny7weauWzZcdlmcgYIPbX30uqxpwzttZXkoO/hLllnHP04feC5fKI8brFDuP3IIDsuL3+ezx2OQJ21GFzGPS1smyOvWH7So3PEdV546zmSZztUSG6tZgsfjyiTSjX11Lnh/duhrTpnXP0uPyVOTzNspjWARxClrYTO7zSFjAdh6UlcZrW4Njo1x0Mzwoc5YRvA9boW8UugCPcg/H9jx3x7OKt3bxKAPArCLrDEf2ZoLJGSsnp2lwfYIKvqSQKMvfM492PRbxFyrZzpUuyD/8e9mfinJoPEozLHk+YKZiQwvLWxp8iCW8NWlP5JOFwMheblPlPHWvnX1q5F9X7Qu5s59pUJjFq4TiS3vK6BdOwjjS0jOmX8="),
        HGMIX("§d§lMIX", "§6HG", new ItemStack(Material.RED_MUSHROOM),
                "ewogICJ0aW1lc3RhbXAiIDogMTYxMDY2MDU5ODUxMiwKICAicHJvZmlsZUlkIiA6ICJjZGM5MzQ0NDAzODM0ZDdkYmRmOWUyMmVjZmM5MzBiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXdMb2JzdGVycyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82YjBiMDZkZTVhZjJmMWFhNTZlYjZlMDgwNGZhNDQ4ZGZjZmZjNjlkYjFmZDI2MjIwYWQ5NmM5ODFjYzY1NWU0IgogICAgfQogIH0KfQ==",
                "RQIM8/0b4mTQSna3kYsQdA/JC4koUk2QfCBJW9KZJO7DrlV8GcyzV4EgKW4GBaZOOuRPu7ZAJu+T0PMcPVEY+rsUmfR46bYtD+VdfkNzcT+EMFhVEbQSm+ygKEtoSEVZpoNDr8PBtBmXB5rSEjlnqqIxPWzzxbcOPfx1BliYt0Kmc8moS7h8KQ899bD/FqzcvI6V2umS9lTVH/BosLFxhhqBhiSG0z19Evk7i3OOlPHSh4MRGYUnWN0a0Npj3z0ntdcRkTSsmtju/x4v4hDPFl1iwwMScRplLgX5zIxvNTyNwrRSLDZNoZ9DP2BzoCUL2n8UZM69096QXdb1Y6fvBlhaZ8w0NnigXuzHTXSbGVK3ct0pDbmg5smpx3bPO3IfEKQIjldIViLOcsIi1BOaj90BfvuH9g77JaxhcclyvnhzKLa14Y17GjuqUBfnOrzTEzLzV/32TQe9TjNVoRn6+/j0zxVFMHFCcliTKTl1y0at7bIIlM5ZRDXS9J3rYlwrXPmDGMZm5CONOtLQ4iXqHGeNO4X0Hq7jYkX+WHmu0tna7AnDCLognR9LYV0Dyr/SZP08anMRJcrAbARrmmW7SoYX+hv1XitPgRmrfcfIHCnPDO+en3UcOIoCPvx0w4Bypp5IikRGpIyhDpzsueLzBdBk+b9bzy+qtr+ZNfeNkO0=");

        private String spotlight;
        private String title;
        private ItemStack itemInHand;
        private String value;
        private String signature;

        NonPlayerType(String spotlight, String title, ItemStack itemInHand, String value, String signature) {
            this.spotlight = spotlight;
            this.title = title;
            this.itemInHand = itemInHand;
            this.value = value;
            this.signature = signature;
        }

        public boolean hasSpotlight() {
            return this.spotlight != null && !this.spotlight.isEmpty();
        }
    }
}
