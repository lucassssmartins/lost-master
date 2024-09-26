package com.lostmc.bukkit.hologram;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.hologram.api.Hologram;
import com.lostmc.bukkit.hologram.api.protocol.ProtocolHook;
import com.lostmc.bukkit.hologram.craft.PluginHologram;
import com.lostmc.bukkit.hologram.craft.PluginHologramManager;
import com.lostmc.bukkit.hologram.listener.HologramListener;
import com.lostmc.bukkit.hologram.nms.NmsManagerImpl;
import com.lostmc.bukkit.hologram.nms.interfaces.NMSManager;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Gerenciador de Hologramas.
 *
 * @author skyprogrammer
 */
@Getter
public class HologramManager extends Controller {

    private NMSManager nmsManager;
    private static List<Integer> idRegistry = new ArrayList<>();

    public HologramManager(Control control) {
        super(control);
    }

    public static List<Integer> getRegistrys() {
        return Collections.unmodifiableList(idRegistry);
    }

    public static void registerId(Integer id) {
        if (!idRegistry.contains(id)) {
            idRegistry.add(id);
        }
    }

    public static void unregisterId(Integer id) {
        idRegistry.remove(id);
    }

    public static boolean hasRegistry(int id) {
        return idRegistry.contains(id);
    }

    @Override
    public void onEnable() {
        nmsManager = new NmsManagerImpl();
        try {
            nmsManager.setup();
            ProtocolHook.load(nmsManager);
            registerListener(new HologramListener(nmsManager));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Hologram createHologram(Plugin plugin, Location source) {
        PluginHologram hologram = new PluginHologram(source, plugin);
        PluginHologramManager.addHologram(hologram);
        return hologram;
    }

    public boolean isHologramEntity(Entity bukkitEntity) {
        Validate.notNull(bukkitEntity, "bukkitEntity");
        return nmsManager.isNMSEntityBase(bukkitEntity);
    }

    public Collection<Hologram> getHolograms(Plugin plugin) {
        Validate.notNull(plugin, "plugin");
        return PluginHologramManager.getHolograms(plugin);
    }

    @Override
    public void onDisable() {

    }
}
