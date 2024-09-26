package com.lostmc.bukkit.hologram.listener;

import java.util.Map;
import java.util.logging.Level;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.hologram.api.protocol.WrapperPlayClientUseEntity;
import com.lostmc.bukkit.hologram.craft.PluginHologram;
import com.lostmc.bukkit.hologram.craft.PluginHologramManager;
import com.lostmc.bukkit.hologram.craft.line.CraftTouchSlimeLine;
import com.lostmc.bukkit.hologram.nms.entity.NMSEntityBase;
import com.lostmc.bukkit.hologram.nms.interfaces.NMSManager;
import com.lostmc.bukkit.hologram.util.Utils;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

/**
 * Forked from https://github.com/filoghost/HolographicDisplays
 */
public class HologramListener implements Listener {

    private NMSManager nmsManager;
    private Map<Player, Long> anticlickSpam = Utils.newMap();

    public HologramListener(NMSManager nmsManager) {
        this.nmsManager = nmsManager;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(BukkitPlugin.getInstance(),
                        ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player p = event.getPlayer();
                        WrapperPlayClientUseEntity packet =
                                new WrapperPlayClientUseEntity(event.getPacket());
                        Entity entity = packet.getTarget(p.getWorld());
                        if (entity == null)
                            return;
                        if (!HologramManager.hasRegistry(entity.getEntityId()))
                            return;
                        NMSEntityBase entityBase = nmsManager.getNMSEntityBase(entity);
                        if (entityBase == null)
                            return;
                        if (entityBase.getHologramLine() instanceof CraftTouchSlimeLine) {
                            event.setCancelled(true);
                            CraftTouchSlimeLine touchSlime = (CraftTouchSlimeLine) entityBase.getHologramLine();
                            if (touchSlime.getTouchablePiece().getTouchHandler() != null && touchSlime.getParent().getVisibilityManager().isVisibleTo(event.getPlayer())) {
                                Long lastClick = anticlickSpam.get(event.getPlayer());
                                if (lastClick != null && System.currentTimeMillis() - lastClick.longValue() < 100L)
                                    return;
                                anticlickSpam.put(event.getPlayer(), Long.valueOf(System.currentTimeMillis()));
                                try {
                                    touchSlime.getTouchablePiece().getTouchHandler().onTouch(event.getPlayer());
                                } catch (Exception ex) {
                                    Plugin plugin = (touchSlime.getParent() instanceof PluginHologram) ? ((PluginHologram)touchSlime.getParent()).getOwner() : BukkitPlugin.getInstance();
                                    plugin.getLogger().log(Level.WARNING, "The plugin " + plugin.getName() + " generated an exception when the player " + event.getPlayer().getName() + " touched a hologram.", ex);
                                }
                            }
                        }
                    }
                });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        byte b;
        int i;
        Entity[] arrayOfEntity;
        for (i = (arrayOfEntity = event.getChunk().getEntities()).length, b = 0; b < i; ) {
            Entity entity = arrayOfEntity[b];
            if (!entity.isDead()) {
                NMSEntityBase entityBase = this.nmsManager.getNMSEntityBase(entity);
                if (entityBase != null)
                    entityBase.getHologramLine().getParent().despawnEntities();
            }
            b++;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        PluginHologramManager.onChunkLoad(chunk);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.nmsManager.isNMSEntityBase(event.getEntity()) &&
                event.isCancelled())
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (this.nmsManager.isNMSEntityBase(event.getEntity()) &&
                event.isCancelled())
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (this.nmsManager.isNMSEntityBase(event.getEntity()) &&
                event.isCancelled())
            event.setCancelled(false);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        this.anticlickSpam.remove(event.getPlayer());
    }
}
