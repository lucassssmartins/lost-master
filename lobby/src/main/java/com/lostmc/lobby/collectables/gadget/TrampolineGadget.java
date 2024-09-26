package com.lostmc.lobby.collectables.gadget;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.utils.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrampolineGadget extends Gadget {

    static List<TrampolineGadget> runningTrampolines = new ArrayList<>();

    private Set<BlockState> trampoline = new HashSet<>();
    private Area cuboid;
    private Location center;
    private boolean running;
    private BukkitTask task;
    private Listener listener;

    public TrampolineGadget(Player owning) {
        super(owning);
    }

    public void spawn(Location where) {
        addGadgetToOwningPlayer();
        cuboid = new Area(center = where, 2, 15);
        generateStructure();
        this.owning.teleport(where.clone().add(0, 4, 0));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(owning)) {
                if (cuboid.contains(p.getLocation().getBlock())) {
                    p.teleport(where.clone().add(0, 4, 0));
                    p.sendMessage("§aYuupiii!!!");
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(listener = new Listener() {

            @EventHandler
            public void onTimer(ServerTimerEvent event) {
                if (event.getCurrentTick() % 10 != 0)
                    return;
                if (running && cuboid != null) {
                    for (Entity entity : center.getWorld().getNearbyEntities(center, 4, 4, 4)) {
                        Block b = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                        if (b.getType().toString().contains("WOOL") && cuboid.contains(b)) {
                            entity.setVelocity(new Vector(0, 2.5, 0));
                        }
                    }
                }
            }
        }, BukkitPlugin.getInstance());
        runningTrampolines.add(this);
        running = true;
    }

    public static boolean checkRequirements(Player player, Location block) {
        Location loc1 = player.getLocation().add(2, 15, 2);
        Location loc2 = player.getLocation().clone().add(-3, 0, -2);
        Block ladder1 = loc1.getBlock().getRelative(3, 0, 0);
        Block ladder2 = loc1.getBlock().getRelative(3, 1, 0);
        Area checkArea = new Area(loc1, loc2);
        if (!checkArea.isEmpty() || !ladder1.getType().equals(Material.AIR) || !ladder2.getType().equals(Material.AIR)) {
            player.sendMessage(Translator.tl(Profile.getProfile(player).getLocale(),
                    "hub.collect-gadget.default-trampoline.not-good-area"));
            return false;
        }
        for (TrampolineGadget trampoline : runningTrampolines) {
            if (block.distance(trampoline.center) <= 7) {
                player.sendMessage("§cHá um trampolim próximo, escolha um lugar mais distante.");
                return false;
            }
        }
        return true;
    }

    private void generateStructure() {
        genBarr(get(2, 0, 2));
        genBarr(get(-2, 0, 2));
        genBarr(get(2, 0, -2));
        genBarr(get(-2, 0, -2));

        genBlue(get(2, 1, 2));
        genBlue(get(2, 1, 1));
        genBlue(get(2, 1, 0));
        genBlue(get(2, 1, -1));
        genBlue(get(2, 1, -2));
        genBlue(get(-2, 1, 2));
        genBlue(get(-2, 1, 1));
        genBlue(get(-2, 1, 0));
        genBlue(get(-2, 1, -1));
        genBlue(get(-2, 1, -2));
        genBlue(get(1, 1, 2));
        genBlue(get(0, 1, 2));
        genBlue(get(-1, 1, 2));
        genBlue(get(1, 1, -2));
        genBlue(get(0, 1, -2));
        genBlue(get(-1, 1, -2));

        genBlack(get(0, 1, 0));
        genBlack(get(0, 1, 1));
        genBlack(get(1, 1, 0));
        genBlack(get(0, 1, -1));
        genBlack(get(-1, 1, 0));
        genBlack(get(1, 1, 1));
        genBlack(get(-1, 1, -1));
        genBlack(get(1, 1, -1));
        genBlack(get(-1, 1, 1));

        genStair(get(-3, 1, 0));
        genStair(get(-4, 0, 0));

        this.task = Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), () -> {
            this.running = false;
            remove();
        }, 20 * 20);
    }

    private void genBarr(Block block) {
        setToRestore(block, Material.FENCE, (byte) 0);
    }

    private void genBlue(Block block) {
        setToRestore(block, Material.WOOL, (byte) 11);
    }

    private void genBlack(Block block) {
        setToRestore(block, Material.WOOL, (byte) 15);
    }

    private void genStair(Block block) {
        setToRestore(block, Material.getMaterial(135), (byte) 0);
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof Directional) {
            ((Directional) data).setFacingDirection(BlockFace.EAST);
            state.update(true);
        }
    }

    private void setToRestore(Block block, Material material, byte data) {
        trampoline.add(block.getState());
        block.setType(material);
        if (data != 0) {
            BlockState state = block.getState();
            state.setRawData(data);
            state.update(false, true);
        }
    }

    @Override
    public void remove() {
        if (this.running && this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        HandlerList.unregisterAll(listener);

        listener = null;

        if (center != null) {
            get(-3, 0, 0).setType(Material.AIR);
            get(-3, 1, 0).setType(Material.AIR);
        }

        for (BlockState state : trampoline) {
            state.update(true);
        }

        trampoline.clear();
        cuboid = null;
        running = false;

        runningTrampolines.remove(this);

        removeGadgetFromOwningPlayer();
    }

    private Block get(int x, int y, int z) {
        return center.getBlock().getRelative(x, y, z);
    }
}
