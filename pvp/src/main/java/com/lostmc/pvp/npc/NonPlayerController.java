package com.lostmc.pvp.npc;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.api.handler.TouchHandler;
import com.lostmc.bukkit.hologram.api.line.TextLine;
import com.lostmc.bukkit.hologram.craft.line.CraftTextLine;
import com.lostmc.bukkit.npc.NpcController;
import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.api.skin.Skin;
import com.lostmc.bukkit.npc.api.state.NPCSlot;
import com.lostmc.bukkit.npc.internal.NPCBase;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import com.lostmc.pvp.warp.registry.FPSWarp;
import com.lostmc.pvp.warp.registry.FightWarp;
import com.lostmc.pvp.warp.registry.LavaWarp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
    public void onJoinListener(PlayerJoinEvent event) {
        nonPlayerMap.values().forEach(npc -> npc.show(event.getPlayer()));
    }

    @EventHandler
    public void onTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        WarpController controller = getControl().getController(WarpController.class);
        for (Map.Entry<NonPlayerType, Hologram> entry : hologramMap.entrySet()) {
            switch (entry.getKey()) {
                case ARENA: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + controller.getWarpByClass(ArenaWarp.class).getPlayerCount()
                            + " jogando agora");
                    break;
                }
                case FPS: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + controller.getWarpByClass(FPSWarp.class).getPlayerCount()
                            + " jogando agora");
                    break;
                }
                case LAVA: {
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + controller.getWarpByClass(LavaWarp.class).getPlayerCount()
                            + " jogando agora");
                    break;
                }
                case MLG:
                    break;
                case SIMULATOR:
                    break;
                case DAMAGE:
                    break;
                case FIGHT:
                    Hologram hologram = entry.getValue();
                    ((TextLine) hologram.getLine(0)).setText("§e" + controller.getWarpByClass(FightWarp.class).getPlayerCount()
                            + " jogando agora");
                    break;
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

            NpcController npcManager = PvP.getControl().getController(NpcController.class);
            NPC npc = npcManager.createNPC().setLocation(location).setItem(NPCSlot.MAINHAND, nonPlayer.handItem)
                    .setSkin(new Skin(nonPlayer.value, nonPlayer.signature)).create();
            getServer().getOnlinePlayers().forEach(p -> npc.show(p));

            this.nonPlayerMap.put(nonPlayer, npc);

            Location hologramL = location.clone().add(0, 1.85, 0);
            if (!hologramL.getChunk().isLoaded()) {
                hologramL.getChunk().load(true);
            }

            HologramManager manager = getControl().getController(HologramManager.class);
            Hologram hologram = manager.createHologram(getPlugin(), hologramL);

            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine("§e...");
            hologram.appendTextLine(nonPlayer.title);
            if (nonPlayer.hasSpotlight()) {
                hologram.appendTextLine(nonPlayer.spotlight);
            }

            hologramMap.put(nonPlayer, hologram);

            if (nonPlayer.equals(NonPlayerType.ARENA)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ArenaWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(ArenaWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.FPS)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    FPSWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(FPSWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.LAVA)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    LavaWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(LavaWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.FIGHT)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    FightWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(FightWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            }
        }
        registerListener(this);
    }

    public void saveLocation(Location location, NonPlayerType nonPlayer) {
        NPC npc;
        if (nonPlayerMap.containsKey(nonPlayer)) {
            nonPlayerMap.get(nonPlayer).setLocation(location);
            Hologram hologram = this.hologramMap.get(nonPlayer);
            hologram.teleport(location.clone().add(0, 1.85, 0));
        } else {
            NpcController npcManager = PvP.getControl().getController(NpcController.class);
            npc = npcManager.createNPC().setLocation(location).setItem(NPCSlot.MAINHAND, nonPlayer.handItem)
                    .setSkin(new Skin(nonPlayer.value, nonPlayer.signature)).create();
            getServer().getOnlinePlayers().forEach(p -> npc.show(p));

            this.nonPlayerMap.put(nonPlayer, npc);

            Location hologramL = location.clone().add(0, 1.85, 0);
            if (!hologramL.getChunk().isLoaded()) {
                hologramL.getChunk().load(true);
            }

            HologramManager manager = getControl().getController(HologramManager.class);
            Hologram hologram = manager.createHologram(getPlugin(), hologramL);

            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine("§e...");
            hologram.appendTextLine(nonPlayer.title);
            if (nonPlayer.hasSpotlight()) {
                hologram.appendTextLine(nonPlayer.spotlight);
            }

            hologramMap.put(nonPlayer, hologram);

            if (nonPlayer.equals(NonPlayerType.ARENA)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    ArenaWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(ArenaWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.FPS)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    FPSWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(FPSWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.LAVA)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    LavaWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(LavaWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
                });
            } else if (nonPlayer.equals(NonPlayerType.FIGHT)) {
                ((NPCBase) npc).setInteractHandler((player, type) -> {
                    FightWarp warp = getControl().getController(WarpController.class)
                            .getWarpByClass(FightWarp.class);
                    warp.joinPlayer(player,
                            ((PvPGamer) Profile.getProfile(player).getResource(Gamer.class)).getWarp());
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

        ARENA("", "§6Arena", new ItemBuilder(Material.STONE_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).build(),
                "ewogICJ0aW1lc3RhbXAiIDogMTYxODIzMjI5MDg3MCwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNmNGMzN2JiYTA1YzVmZWVkNDdkZjk1MmViZGRhYjNhN2NlYjUyNDRjYTQ5MDc0MTkzN2FjYTM5MjQ3NDU4ZCIKICAgIH0KICB9Cn0=",
                "PSTTrls8Ny/9XEKvYYxTaVnBG0jVwDbtJC3jU+sOK6bW8q1noiZQyRh3pNRJkPBsBj1ncNM+yy2doEY/NaVn+w7zM47pdnYG+KH7bK6z5lqixsg2VvhAuFN7kTWljtukBrjWSIIysNRmoKvEMa8lMMNniqNoG3F25hJfFcQZt2t54hwIWC5hOHq+bA3yDLvglD58cIRY2+JZ1MCh14T3ca549psz29Kp8gB6IqIHNA8sJEqQIPP+VkXY/L36Tk11Em7g64zDETi8TAkLRp8yQ2Q29DPbUFi87V5R+7oNJ3uHDn7tC5q5WXOwZ1cYpFuKnR3wqJfXWD2N1lxzb+y1/jiIneVGLP63S4pvq4571GPCZ5tMcr0qVSJrtsZJmbSAMeTWdZ8VDYIODAZ/TkDAAxFL902TJTue8yLKnTZE2p3d12PV25nPkDhGSIqienro6qqofvsNwCRKd6y53mydAXgozzae2x8RFzkaYVRo19j9tM52RcTdq4isSJ62D66BWgRe4abX+L32lxmWQ8JklYPhi2LlD7i450dI6Q/vQryCzs20R0LCc+YZ+bA68x1Thy9yXMsT+9dm907abojvEcVyritaiBHtYQw0EjC6i7Zqu4p05+X9FHhC0WFdAZdvy05U1fi6IGRUiWxLtbEaEeKPSKxCN1qseU3B43n1S0Q="),
        FPS("", "§6FPS", new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).build(),
                "ewogICJ0aW1lc3RhbXAiIDogMTYyMTc1ODc0NzE3MCwKICAicHJvZmlsZUlkIiA6ICIxZjU4MDU3MTZkYzA0MGE3OWMyZTUyMTgyYzE0MGU4YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYW11cmFpIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU2YzExZjE5ZjExNmZiYmJhMDZjMGZhMjZkZTFhOWIzNDBkMTFhN2UwMWY1YmViNTJiNTBmZjY5MzZlNDYwYzgiCiAgICB9CiAgfQp9",
                "WIxPOCSzNG/xkE0AMmc7rtTue4KAq9djSImjeFt7hvIla8TZoiVqYruKiawCZj7GpzPBzze1RNPpuek7cSsVhlUPaFHQMw484y3iWbOSP/HH+9VpeT5WTKcl94qXaVHlIl2GWS3KJdstwuIJw7OgFDOyUC9ahhm3z8k8xrWQu3T7jl16iWqzOBlRBgD7B7s869XfN2vEnFWX4KJyiLVNa3coDbwjEWZGaTly7aLJlMkCJk0I8E6MmAuQMLAmFHMLStizX/nXnwTf2+Szd2bep6myW1//3b6k42z++73Mvbh7Len7AlPeQwtpMSt+mOvZymflYAzSH77020ulWDbzA4rtTgguktKMGQzypRLUfjLkOfP8zCFHS/yCDM/XwWnyfr0B1XJRCno4CMErR054OcmsvLnOkJ76VHdMYXKKIUr95MVp6fAxtezlerZiohIOR5kbs1LBUVAYxfRhCcr80en5EL7GwzXUawx35HEkSnqksplBbYIi8oTEuGlvqx2mZeQo/Tt1j4ldOXn6yUC4oVn64JTJLm8tf9xnFSxqMFSgJBHTSF4hTdiaFYlzmdFfSbhNUL61a+Q8tD3RUiU8bBKH3Zj992o9fmb3bm/R6ynPgiKl1dHVKNf3DXdn1MnNx30rljPbhf5Q06Wsld61j6hM8p5j+7mFZvH39NLZvn0="),
        LAVA("", "§6Lava", new ItemStack(Material.LAVA_BUCKET), "eyJ0aW1lc3RhbXAiOjE1NTk5NjUyNDY4OTgsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzliMWI0MmI2NGIxYzYxMDAwNTI0YWQ0MTZiNGFhMzMzMzkxMzVjODQ1Mzg2ODRhODhlMjhlYTEyMGUyYjVhMDcifX19",
                "qr4CfPKqjhIRCzj6JehRUEv0zLVVJg+XC4s6EWU4qRr91BtHKRhNRDbwfuIeSRjM2qsb2rZdieeemDEZl805QOmY1wkWk1BviETTeX6HheSw/i6xt1AkuKo1s6Ehti2A9EsxsYeEwkZ+AHorvtWmmE46zz9P0ztr+K0DYgGf24soUb5y+Bwkl1Q6wekyzExH9E9qUUGOfUZSdgjzhvQeSAW+/CLdfPSeCO4kxUf01a95FVvHTH7srPeOdTz5PGii5DrWTmPmb8pdU2lI1JjCcVZuYJGjwIBfAe1733wds9ouRnOrY2Rm4XzT3k/F38A2HgkbyV+w8zMUxvEUHiWnD+5cRH9sr2ES8lqlbZb6LDlrQ2sOcO/jPSJ9XP6zuAIrs3VxVrQwueUyhWxRsdVOxgH9fA1VlbpHqo8NMOwtYq1xiaL7VsNJjA+9l0HOEtqkg/Fb4hvxV7HNNisUWtF1Fjbdrb3qACISEG3WBbbg7IpX92zy9OCKOOt4T/RzRiuYzKB9s+h6ArmsOqD8LfmVfCcgVh4Voiy0FO/JrkURF854M3FfnGfLMhvkkp4XWp9a1AgZX8yPBOPMEdZ39f2/wL7luFwtbhxiPksC1HfqDwzTVsnJRcYcRxV9k2FcDDDkfoXJZ+QryGgeHb1sI6YWgq+stL6/X92efN10sxDCB4Q="),
        MLG("", "§6MLG", new ItemStack(Material.WATER_BUCKET), "ewogICJ0aW1lc3RhbXAiIDogMTY1MjM4Mzg2NzgzMSwKICAicHJvZmlsZUlkIiA6ICIxNGU0NGFmZjZhODg0ZGFmOGEyNDUzYzIwZDVhNTE1YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKYW4yazE3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1YTFmOTMzOGFkNGQ1MDFmODNkOTJlMjViOWZlMDJhMTc3YjA5NzU4NTViZWIzYTI4OGIyNWM2MzFlNDUyNmYiCiAgICB9CiAgfQp9",
                "rdOhWGlAY7sEvSfj3g4+7gIGJjEQ+YZ/3shy0+RjijPZ8glBoUo6a6VPTHZ1uvI3ogcBplOa7fvu6K0DKsC94SGNjzplZVc3h6eVESvRRl6gRlcHLDfyxd6z+Oo61oypYIRy2qmWY4aS8ynAyn6+VQyYnhumbq3gjyyte+hO9WHy+DMdAazbiCOYinKHem6mgXYR+bD8gnKtVO1kjadJX7rqjSoaKAx4B9RYx5FhdQHHruy8KRsJxUmDYCIx9QA57/p1CgzCCBjvR1XWbVnJeEZ9U5OXk2D+NakkN0SNW0DUUu8pLprQrps63nfxcb4EXasc6KAsKy3vA2pm/MecVlblIXusa29fyVXoveBa8WTGPDC+lXjJx5FIDrCeY7I9s2PS1+2dy3PQo+y+cpwrBYEKXM2DjNzrz0UnGNbgErsRAUb0BgYbmbb9/KwqNe8gkMO6tRm6rjKDOUrJljAYOf4GpFy5JwXHM+0eDblEaMp0yr09EtcnAS7eW/JNmnVLJGq3HZgLREQ+cS+4ERO1OoO9RfOYvT/mKeo1AJA833Xxn298HE6Yhv5+E9sbFehsC+dcn7jM4xtj3YW7eIhk3FguAqS9rkRtg81JD3KCkGQz2Og8LAED3MkLj9fOOJa5ws1ra0fLp5uO7L9DZspmFWuu+PsBuZUCZgqNhq+HNQI="),
        SIMULATOR("§d§lNOVO", "§6Simulator", new ItemStack(Material.RED_MUSHROOM), "ewogICJ0aW1lc3RhbXAiIDogMTY0ODE0ODI5OTkwMiwKICAicHJvZmlsZUlkIiA6ICJjYzJhZTkyODRkMWU0ZDY0YWNkZWU0ZDU0MDRiZjdhMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJpbXBhdGllbnRnYW1lIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVmNDg0MDZkYjlmNzkwNDhhNDlhNDkwOTc3NmI5YmJlZDA0YWZiMmExZjQxZWZiZTgxZDM1MzI5NzEzZjU4ZjMiCiAgICB9CiAgfQp9",
                "qnVTNUZRA8tch5c2raJ7eguPbtgiycaDki5FeD7yfQ81bRfRiEokjNAQR6eIXkc0vYz1GX94wVjLbmOkwghO+f/d2XqHWPfRaF940HKbQ8F4Idr8Ncxqnva0Q2NQsPEG8PCk7LWCrD/LkA/ALrAD/jKPyG3Dl4yv/HQg8kl5D1FojjK8nBciBVjeUcNRL0VtkOW6bwrFKsmslg03dGCNGubmJywwkxW9bkImzxuHic1vEOZs87gJDEelneCQQzlWl8RLTQPyUl2gn3UTPkr2i87BTTszWE5BQiRJWhESXD1ayOzUwpqt+dm5T4NWhzT0eeHYy6/O79M8OpHYE2xGToCMhql7rSjHdx3Bq99ib82pHg9KM1Goz4AuCBkKCNOSmFLAQqRXUWernh+O+4LANUSFqq6whxihzSPfAYX+xMuSKllSLzrEZRVClixKAeGQ/4yXfXBa51TGcEnkjTMxf6DXDuWpf7W7FHLAHeb25tzipLeXidauJ3lU+b/zuG4TjtLic7BooNHv7FXcc8O6NVYqQVWujDb30wqaZfEEzRKLL76wQwj51uFkRPqI6NbBgubzfGvsBMHWCIDsFOSeKi3m+idqArEwHYX/L5hm/odqPeIPR4cI0TcRgkN2h7dLt3og7FUQYgX2iZnz7CSu0JDKxRkjtgxw27edJ1+BBtw="),
        DAMAGE("", "§6Damage", new ItemStack(Material.MUSHROOM_SOUP), "ewogICJ0aW1lc3RhbXAiIDogMTYxMTkyMTE0OTI0MCwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yYTM5N2Q5ZTc4ZmYwYmFjNzA2ZWZhNjA2MGU0Y2NkNWM3ODIyZTczZTk0MDIzOWQ0YjY4ZGE4ZmMzYWQwNDYiCiAgICB9CiAgfQp9",
                "Nq+sbmIYDpA2+1ZJlfqfc+sqDbYs1OZ7FA9mkWKm1tDQJKryPxnU2aXQEo7lGiDqp224NaK/15ABeqRyu3yLG8HyYEcY2F8fPAxEI4WPQkoTZlAP/a8+Yju3HMgeLK+98gjuApYTmL531kctgFRqVTNt+9e9E1yZN4j6xPSV1rgD2Od5bz9Li7FkHuMbV6PP7jM877PclcUdvGt5BY2uKpKazSTLSEVUrTlDe+Fk8FUeC96bYEQzdJI/9pxAA6DeP7FwMS3e9uzYPF144IDGZJ+9FwO2jDcDpl6XAgd0fuCqQpRHsqxh5OBZAfJ3Ac8qgLUUH73ip7UocV82kKX4F0SHF8oY6uzR9SjgPhGQVNtMXH3jibE+5MddIGEmDJ7IHPXLEIR3A8OpgzGyzaK9HJt6qrhLDb4WDCOkZunpmobwEVjPxBz3oNISupnbPIoUr7XD9J0xmLAQ4tw62hO2odf16BRgnBuhPY5JHE6PJh8A0IfOQv+CipGeQaYAU2iBTkMe9A0C93AtFVJx6Cujb83oP2BmPGycvuqn0dKftO0fJqFB5xEwUT59BVjNlwQmABxvjHbor52K3OdOtPVpSLyBY8Zw0SW/NWNm1c7tCGUjoiqE+xO2/TVCpNLe6XzVQM3aiBypVpW/JTakVqMpPbTDRfwMGp3/olY1oF4id9c="),
        FIGHT("§d§lRANKED", "§61V1", new ItemStack(Material.IRON_CHESTPLATE), "ewogICJ0aW1lc3RhbXAiIDogMTYyNjgyMjU0NzAzMSwKICAicHJvZmlsZUlkIiA6ICI1NjY3NWIyMjMyZjA0ZWUwODkxNzllOWM5MjA2Y2ZlOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVJbmRyYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZDU2ZThmZDA4ZmZmNDcxMDY4YWJhOTI5ZWVkNDU2MWUzNjQ4N2RiNzc3ZDljOTYzZDM1YzMwZTdkYTdiNmFkIgogICAgfQogIH0KfQ==",
                "V7M6ToJyp3pP+AJkFa57Yx8uWl/3Ef10Xn3ozwY8rfoBoG3N/2P7787R+GBsxd/5B4FJxOJiwvtVDykD1YyQxO4ODH0iasmduT7nlXfCL3Ti4n9dt4NfPWKI1LuQKWWzrVjNOu/Dx2MnJBE926KVtMrBetFlKLH7bELPDz5Z9Tw3sXmgtZEqaz3PmK/YJi6PHlgT0xCYBZ733sILiBo7e2iTsib4/W7/tFJUETDOVORD/5nXavJ+ECNPGtTcO8rPwKNFRLAhHoVab4iKwcxxAKcYdye3l6Kb77WBQETb891LSOYA5UdAz9RdmTQYwWvWd9VBPQEUjFSjJ1dSFLnf1PW9gjEeGLGL86smx+3rVOj1IX0QixGOpXYiqNq0Ei3t+7MSd3et0fzXGu5npRmFGd4S3pQHVr2zGDnA15flzLI/3t1Q2YIB05PibgQ42GW+I730kGBjrYj2BVF6xSduCEnVjLPVYaHaljU43DrCyqA7KWUy2uVMOECXqw26KDfwb93mh8ECrl/OOiySlq4bAjkLV0wvEyOYmcHx4C/2iMDYnvyb/g88KVgVnvnzdyOFFGWPMqQIyVDMA5eA4Pfx9wRwI5OGZWpyqBwFyQpPBxaiC5iIiutvf6IDAmj+xBcIHILilCDJ+FBYpNNqcIDvXKT8VWOl+yrb9L/T6ZKZOwY=");

        private String spotlight;
        private String title;
        private ItemStack handItem;
        private String value;
        private String signature;

        NonPlayerType(String spotlight, String title, ItemStack handItem, String value, String signature) {
            this.spotlight = spotlight;
            this.title = title;
            this.handItem = handItem;
            this.value = value;
            this.signature = signature;
        }

        public boolean hasSpotlight() {
            return this.spotlight != null && !this.spotlight.isEmpty();
        }
    }
}
