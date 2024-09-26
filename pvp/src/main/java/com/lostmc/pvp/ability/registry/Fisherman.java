package com.lostmc.pvp.ability.registry;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.game.GamePlugin;
import com.lostmc.pvp.ability.CustomPvPKit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class Fisherman extends CustomPvPKit {

    public Fisherman(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.FISHING_ROD);
        setItems(new ItemBuilder(Material.FISHING_ROD).setUnbreakable(true).setName("§aFisherman").build());
        setBuyPrice(20000);
        setRentPrice(2000);
        setIncompatibleKits("Neo");
        setDescription("Use sua vara de pesca para fisgar outros jogadores.");
    }

    @Override
    public ItemStack getMainItem() {
        return getItems()[0];
    }

    @EventHandler
    public void onPlayerFishListener(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (isUsing(e.getPlayer())) {
                if (e.getCaught() instanceof Player) {
                    Player c = (Player) e.getCaught();
                    World w = e.getPlayer().getLocation().getWorld();
                    double x = e.getPlayer().getLocation().getBlockX() + 0.5D;
                    double y = e.getPlayer().getLocation().getBlockY();
                    double z = e.getPlayer().getLocation().getBlockZ() + 0.5D;
                    float yaw = c.getLocation().getYaw();
                    float pitch = c.getLocation().getPitch();
                    Location loc = new Location(w, x, y, z, yaw, pitch);
                    c.teleport(loc);
                }
            }
        }
    }
}
