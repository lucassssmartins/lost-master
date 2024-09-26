package com.lostmc.pvp.ability;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.utils.ClassGetter;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Kit;
import com.lostmc.pvp.PvP;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class KitController extends Controller {

    @Getter
    private List<Kit> kits = new LinkedList<>();

    public KitController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        for (Class<?> clazz : ClassGetter.getClassesForPackageByFile(PvP.PLUGIN_FILE,
                "com.lostmc.pvp.ability.registry")) {
            if (!Kit.class.isAssignableFrom(clazz))
                continue;
            try {
                registerAbility((Kit) clazz.getConstructor(GamePlugin.class)
                        .newInstance(getPlugin()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerAbility(Kit ability) {
        registerListener(ability);
        this.kits.add(ability);
    }

    @Override
    public void onDisable() {
        this.kits.clear();
        this.kits = null;
    }
}
