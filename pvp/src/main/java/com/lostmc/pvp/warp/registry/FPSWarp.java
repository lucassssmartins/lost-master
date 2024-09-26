package com.lostmc.pvp.warp.registry;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.WarpScoreboardModel;
import com.lostmc.pvp.warp.event.PlayerDeathInWarpEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FPSWarp extends Warp {

    private final ItemStack[] armors = {stack(Material.IRON_BOOTS), stack(Material.IRON_LEGGINGS),
            stack(Material.IRON_CHESTPLATE), stack(Material.IRON_HELMET)};
    private final ItemStack BACK_TO_SPAWN = new ItemBuilder(Material.BED).setName("§aVoltar ao spawn")
            .build(new InteractHandler() {

                @Override
                public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                    player.performCommand("spawn");
                    return true;
                }
            });

    public FPSWarp(WarpController controller) {
        super(controller);
        setName("FPS");
        setFallKitWarp(true);
        setScoreboardModelClass(FPSScoreboardModel.class);
    }

    private ItemStack stack(Material m) {
        return new ItemStack(m);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathInWarpEvent e) {
        if (e.getWarp().equals(this)) {
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathInCombatEvent e) {
        if (containsPlayer(e.getCombatPlayer()) && !isProtected(e.getCombatPlayer())) {
            e.getCombatPlayer().getInventory().setArmorContents(armors);
            e.getCombatPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onInventoryClickListener(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (containsPlayer((Player) event.getWhoClicked()) && isProtected((Player) event.getWhoClicked())) {
                if (event.getCurrentItem() != null && event.getCurrentItem().equals(BACK_TO_SPAWN)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.getInventory().setItem(8, BACK_TO_SPAWN);
        player.getInventory().setHeldItemSlot(0);
    }

    @Override
    public void onProtectionLost(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(armors);
        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD)
                .addEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .build());
        ItemStack soup = new ItemBuilder(Material.MUSHROOM_SOUP).build();
        for (int i = 0; i <= 35; i++)
            player.getInventory().addItem(soup);
        player.getInventory().setItem(13, new ItemBuilder(Material.BOWL).setAmount(64).build());
        player.getInventory().setItem(14, new ItemBuilder(Material.RED_MUSHROOM).setAmount(64).build());
        player.getInventory().setItem(15, new ItemBuilder(Material.BROWN_MUSHROOM).setAmount(64).build());
        player.updateInventory();
    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    public static class FPSScoreboardModel extends WarpScoreboardModel {

        public FPSScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP: FPS");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add("§fKills: §7" + String.format("%,d", profile.getData(DataType.PVP_GLOBAL_KILLS)
                    .getAsInt()));
            perPlayer.add("§fDeaths: §7" + String.format("%,d", profile.getData(DataType.PVP_GLOBAL_DEATHS)
                    .getAsInt()));
            perPlayer.add("§fKillstreak: §a" + profile.getData(DataType.PVP_GLOBAL_KS).getAsInt());

            perPlayer.add("");

            perPlayer.add("§fCoins: §6" + String.format("%,d", profile.getData(DataType.COINS).getAsInt()));

            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }
}
