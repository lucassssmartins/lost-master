package com.lostmc.pvp.warp.registry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.protocol.ProtocolVersion;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.kit.KitMenuType;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.menu.KitMenu;
import com.lostmc.pvp.menu.StoreMenu;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.WarpScoreboardModel;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

import static com.lostmc.core.translate.Translator.tl;

public class SpawnWarp extends Warp {

    private static final GameProfile chestProfile;

    static {
        chestProfile = new GameProfile(UUID.randomUUID(), null);
        chestProfile.getProperties().put("textures", new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTY1MjM1NjUxNjIwMCwKICAicHJvZmlsZUlkIiA6ICI0NGNlMmMwZTFjNTM0ZDhmYmExNmNkNDhlYjkyYTUxZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4WGlyYW56dThYeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kOGI4YzYxZTk4ZTdjMzM4MDM3OGMzM2ZiMTQzY2ZiZDg5NjlhMjcyNDBiZjdlNjIyZmE0ZjJiMTU3MzRjMGMwIgogICAgfQogIH0KfQ==",
                "BZNmzFR+953QWTz6UlBOLrZBlOFlW2IVTi7Kuw17Xs16z8TKVE76Cyf5UCbgjDGASNAye1lGRgKJfZbNiSBQGtEMSvnGbowKXAaDKUKoWI0aPD3RlcTCudRqGhQknR8XlBdG64yufgttJksm3tWLlItqeUSmz3vydYJzwLJB4Kt8TlCSTOun+6kro4hHF1TsRX3rucb/CXP5RaubssO208DZ/IEZ/MN3WJTuVZUsv3g5ldHBXi9s7ylA6Jva+BSZyaLt8NQUT1Wfx95MMBSNbgMf98nInt4DGxl+DFOrvPOLSBJCU5AQFh2m/fIYf2j7sTdJKehtBkWyaS6bAqQFwwXg0B7l2RyXOP6scvx9FUGc7NzAmuf+fwzos1Dx5H5hoDVzrakDGHkfhvAsx96fTLWgQpE9N+cE3+kOn6lNZlE5aXNhi7niLLQvi6BxM34FOHgrlCMT78jn3KO3hnvZkkLYzWYCWN4qqsXqlHR1hy0xC7XSfwLP+c84kI9X8L653Ucp1dsdM8g3/eKIPCaZmtcYhYHwWgkRl9QxbUvzzVHsdFFvcibEotnFOKMUpYmbVho1/5IzMWOtuIW4ZUEOR3WRU4UzeYgzqdR7iHC8wNkbqqjPCEfa9pFezMKaJVAgidGrJljp7UHs2U+RIbif8aQSkJ3O4DaQDHGIIBSLq1w="));
    }

    public SpawnWarp(WarpController controller) {
        super(controller);
        setName("Spawn");
        setScoreboardModelClass(SpawnScoreboardModel.class);
    }

    @Override
    public void onPlayerJoin(Player player) {
        Profile profile = Profile.getProfile(player);
        player.getInventory().setItem(0, new ItemBuilder(Material.CHEST)
                .setName(tl(profile.getLocale(), "pvp.spawm-warp-itens.kit-1-selector-name"))
                .build(new InteractHandler() {
                    @Override
                    public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                        player.openInventory(new KitMenu(player, Profile.getProfile(player), 1, KitMenuType.PRIMARY));
                        return true;
                    }
                }));
        player.getInventory().setItem(1, new ItemBuilder(Material.CHEST)
                .setName(tl(profile.getLocale(), "pvp.spawm-warp-itens.kit-2-selector-name"))
                .build(new InteractHandler() {
                    @Override
                    public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                        player.openInventory(new KitMenu(player, Profile.getProfile(player), 1, KitMenuType.SECONDARY));
                        return true;
                    }
                }));
        player.getInventory().setItem(2, new ItemBuilder(Material.EMERALD)
                .setName(tl(profile.getLocale(), "pvp.spawm-warp-itens.store-item-name"))
                .build(new InteractHandler() {
                    @Override
                    public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                        player.openInventory(new StoreMenu(player, Profile.getProfile(player), 1));
                        return true;
                    }
                }));
        player.getInventory().setItem(8, new ItemBuilder(Material.BED)
                .setName(tl(profile.getLocale(), "pvp.spawm-warp-itens.back-to-lobby"))
                .build(new InteractHandler() {
                    @Override
                    public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                        ByteArrayDataOutput data = ByteStreams.newDataOutput();
                        data.writeUTF("Connect");
                        data.writeUTF("main-lobby-01");
                        player.sendPluginMessage(PvP.getInstance(), "BungeeCord", data.toByteArray());
                        return true;
                    }
                }));
    }

    @Override
    public void onProtectionLost(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    public static class SpawnScoreboardModel extends WarpScoreboardModel {

        public SpawnScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add(tl(profile.getLocale(), "pvp.sidebar.welcome"));
            perPlayer.add(tl(profile.getLocale(), "pvp.sidebar.choose-a-warp"));
            perPlayer.add("");
            perPlayer.add("§fEm jogo: §a" + PvP.getControl().getController(WarpController.class).getPlaying());
            perPlayer.add("§fJogadores: §a" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }
}
