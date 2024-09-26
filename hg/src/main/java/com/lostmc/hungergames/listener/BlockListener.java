package com.lostmc.hungergames.listener;

import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.manager.FeastManager;
import com.lostmc.hungergames.manager.MatchManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.structure.Feast;
import com.lostmc.hungergames.structure.FeastStructure;
import net.minecraft.server.v1_8_R3.BlockCocoa;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IBlockState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockListener extends HungerListener {

    private boolean buildEnabled() {
        return Management.getManagement(MatchManager.class).isBuildEnabled();
    }

    private boolean isPlaying(Player player) {
        HungerGamer gamer = HungerGamer.getGamer(player);
        return gamer.isAlive();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!buildEnabled() && isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (isFeastBlock(event.getBlock()))
            event.setCancelled(true);
        else if (buildEnabled() && isPlaying(event.getPlayer()) && event.isCancelled())
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        if (isFeastBlock(event.getBlock()))
            event.setCancelled(true);
        else if (buildEnabled() && isPlaying(event.getPlayer()) && event.isCancelled())
            event.setCancelled(false);
    }

    private boolean isFeastBlock(Block b) {
        if (FeastStructure.isFeastBlock(b)) {
            Feast actual = Management.getManagement(FeastManager.class).getActualFeast();
            if (actual == null)
                return false;
            if (!actual.isChestsSpawned())
                return true;
        }
        return false;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (e.getBlock().getType().equals(Material.BROWN_MUSHROOM)) {
            e.setCancelled(true);
        } else if (e.getBlock().getType().equals(Material.RED_MUSHROOM)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        HungerGamer gamer = HungerGamer.getGamer(player);
        if (!gamer.isAlive())
            return;
        Block block = event.getBlock();
        if (block.getType() == Material.COCOA) {
            IBlockData data = ((CraftChunk) block.getChunk()).getHandle()
                    .getBlockData(new BlockPosition(block.getX(), block.getY(), block.getZ()));
            ItemStack item = new ItemStack(Material.getMaterial(351), (((Integer)data.get((IBlockState) BlockCocoa.AGE)).intValue() >= 2) ? 3 : 1, (short) 3);
            if (item.getAmount() <= getRestingSlotsFor(player.getInventory(), item.getType())) {
                player.getInventory().addItem(new ItemStack[] { item });
                block.setType(Material.AIR);
                event.setCancelled(true);
            } else {
                return;
            }
        }
        Collection<ItemStack> drops = block.getDrops();
        for (ItemStack drop : drops) {
            if (drop.getAmount() <= getRestingSlotsFor(player.getInventory(), drop.getType())) {
                player.getInventory().addItem(new ItemStack[] { drop });
                continue;
            }
            return;
        }
        block.setType(Material.AIR);
        event.setCancelled(true);
    }

    public static int getRestingSlotsFor(Inventory inv, Material type) {
        boolean free64 = false;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                if (item == null || item.getType() == Material.AIR) {
                    if (!free64)
                        free64 = true;
                } else if (item.getType() == type) {
                    if (item.getAmount() < 64)
                        return 64 - item.getAmount();
                }
        }
        if (free64)
            return 64;
        return 0;
    }
}
