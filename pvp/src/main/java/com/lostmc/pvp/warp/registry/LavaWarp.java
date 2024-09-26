package com.lostmc.pvp.warp.registry;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.WarpScoreboardModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LavaWarp extends Warp {

    public LavaWarp(WarpController controller) {
        super(controller);
        setName("Lava");
        setScoreboardModelClass(LavaScoreboardModel.class);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageListener(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player p = (Player) event.getEntity();
        if (containsPlayer(p)) {
            if (event.getCause() != EntityDamageEvent.DamageCause.LAVA) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.getInventory().clear();
        ItemStack soup = new ItemBuilder(Material.MUSHROOM_SOUP).build();
        for (int i = 0; i <= 35; i++)
            player.getInventory().addItem(soup);
        player.getInventory().setItem(13, new ItemBuilder(Material.BOWL).setAmount(64).build());
        player.getInventory().setItem(14, new ItemBuilder(Material.RED_MUSHROOM).setAmount(64).build());
        player.getInventory().setItem(15, new ItemBuilder(Material.BROWN_MUSHROOM).setAmount(64).build());
        player.updateInventory();
    }

    @Override
    public void onProtectionLost(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    public static class LavaScoreboardModel extends WarpScoreboardModel {

        public LavaScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP: LAVA");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add("§fCoins: §a" + String.format("%,d", profile.getData(DataType.COINS).getAsInt()));
            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }
}
