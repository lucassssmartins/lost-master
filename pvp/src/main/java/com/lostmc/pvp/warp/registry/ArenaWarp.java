package com.lostmc.pvp.warp.registry;

import com.google.common.reflect.TypeToken;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.utils.location.ILocation;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.WarpScoreboardModel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaWarp extends Warp {

    @Getter
    private List<ILocation> randomTp = new ArrayList<>();

    public ArenaWarp(WarpController controller) {
        super(controller);
        setName("Arena");
        setScoreboardModelClass(ArenaScoreboardModel.class);

        if (getController().getPlugin().getConfig().contains("pvp.arena-spawns")) {
            List<ILocation> randomTp = Commons.getGson().fromJson(getController().getPlugin()
                    .getConfig().getString("pvp.arena-spawns"),
                    new TypeToken<ArrayList<ILocation>>(){}.getType());
        }
    }

    public void addRandomTp(Location location) {
        Commons.getPlatform().runAsync(() -> {
            if (randomTp == null)
                randomTp = new ArrayList<>();
            randomTp.add(new ILocation(location));
            getController().getPlugin().getConfig().set("pvp.arena-spawns", Commons.getGson().toJson(randomTp));
            getController().getPlugin().saveConfig();
        });
    }

    @Override
    public void onPlayerJoin(Player player) {
        PlayerInventory inventory = player.getInventory();

        Gamer gamer = Profile.getProfile(player).getResource(Gamer.class);
        ItemBuilder builder = new ItemBuilder(Material.MUSHROOM_SOUP);

        for (int i = 0; i <= 35; i++)
            inventory.setItem(i, builder.build());

        Map<Integer, Kit> abilities = gamer.getKits();
        if (abilities.isEmpty()) {
            inventory.setItem(0,
                    builder.setType(Material.STONE_SWORD).setUnbreakable(true).addEnchant(Enchantment.DAMAGE_ALL, 1).build());
        } else {
            inventory.setItem(0,
                    builder.setType(Material.STONE_SWORD).setUnbreakable(true).build());
        }

        int w = 1;
        for (Kit ability : abilities.values()) {
            if (ability.getItems() == null)
                continue;
            for (ItemStack item : ability.getItems()) {
                player.getInventory().setItem(w, item);
                ++w;
            }
        }

        inventory.setItem(13, new ItemBuilder(Material.BOWL).setAmount(24).build());
        inventory.setItem(14, new ItemBuilder(Material.RED_MUSHROOM).setAmount(24).build());
        inventory.setItem(15, new ItemBuilder(Material.BROWN_MUSHROOM).setAmount(24).build());

        setProtected(player, false);
    }

    @Override
    public void onProtectionLost(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    public static class ArenaScoreboardModel extends WarpScoreboardModel {

        public ArenaScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP: ARENA");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add("§fKills: §7" + String.format("%,d", profile.getData(DataType.PVP_GLOBAL_KILLS)
                    .getAsInt()));
            perPlayer.add("§fDeaths: §7" + String.format("%,d", profile.getData(DataType.PVP_GLOBAL_DEATHS)
                    .getAsInt()));

            boolean space = false;

            Gamer gamer = profile.getResource(Gamer.class);
            if (gamer.getKits().containsKey(1)) {
                space = true;
                perPlayer.add("");
                perPlayer.add("§fKit 1: §6" + gamer.getKits().get(1).getName());
            }

            if (gamer.getKits().containsKey(2)) {
                if (!space)
                    perPlayer.add("");
                perPlayer.add("§fKit 2: §6" + gamer.getKits().get(2).getName());
            }

            perPlayer.add("§fKillstreak: §a" + profile.getData(DataType.PVP_GLOBAL_KS).getAsInt());

            perPlayer.add("");

            perPlayer.add("§fCoins: §6" + String.format("%,d", profile.getData(DataType.COINS).getAsInt()));

            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }
}
