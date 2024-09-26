package com.lostmc.hungergames.manager;

import com.lostmc.core.utils.ClassGetter;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.kit.HungerKit;
import com.lostmc.hungergames.kit.registry.Nenhum;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KitManager extends Management {

    private List<Kit> kits = new ArrayList<>();

    public KitManager(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        for (Class<?> clazz : ClassGetter.getClassesForPackageByFile(HungerGames.PLUGIN_FILE,
                "com.lostmc.hungergames.kit.registry")) {
            if (HungerKit.class.isAssignableFrom(clazz)) {
                try {
                    HungerKit kit = (HungerKit) clazz.getConstructor(GamePlugin.class)
                            .newInstance(getPlugin());
                    registerListener(kit);
                    kits.add(kit);
                    getPlugin().getLogger().info("Kit '" + kit.getName() + "' registrado!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        kits.sort((o1, o2) -> {
            if (o1 instanceof Nenhum) {
                return "A".compareTo(o2.getName());
            } else if (o2 instanceof Nenhum) {
                return o1.getName().compareTo("A");
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public List<Kit> getAllKits() {
        return Collections.unmodifiableList(kits);
    }

    public Kit getKitByName(String name) {
        return kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Kit getKitByClass(Class<? extends Kit> clazz) {
        for (Kit kit : kits) {
            if (kit.getClass() == clazz) {
                return kit;
            }
        }
        return null;
    }

    public boolean hasPrimaryKit(Player p, Kit kit) {
        return p.hasPermission("hgkit1." + kit.getName().toLowerCase()) || kit.isFree()
                || kit instanceof Nenhum;
    }

    public boolean hasSecondaryKit(Player p, Kit kit) {
        return p.hasPermission("hgkit2." + kit.getName().toLowerCase()) || kit.isFree()
                || kit instanceof Nenhum;
    }

    public List<Kit> getPrimaryKits(Player p) {
        List<Kit> primaryKits = new ArrayList<>();
        for (Kit kit : kits) {
            if (kit.isFree() || kit instanceof Nenhum || p.hasPermission("hgkit1." + kit.getName().toLowerCase())) {
                primaryKits.add(kit);
            }
        }
        return primaryKits;
    }

    public List<Kit> getSecondaryKits(Player p) {
        List<Kit> secondaryKits = new ArrayList<>();
        for (Kit kit : kits) {
            if (kit.isFree() || kit instanceof Nenhum || p.hasPermission("hgkit2." + kit.getName().toLowerCase())) {
                secondaryKits.add(kit);
            }
        }
        return secondaryKits;
    }

    @Override
    public void onDisable() {

    }
}
