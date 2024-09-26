package com.lostmc.lobby.collectables.gadget;

import com.lostmc.core.Commons;
import com.lostmc.lobby.Lobby;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class FireworkGadget extends Gadget {

    private BukkitTask task;
    private int tick = 0;

    public FireworkGadget(Player owning) {
        super(owning);
    }

    @Override
    public void spawn(Location where) {
        addGadgetToOwningPlayer();
        launch();
        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                if (++tick >= 10)
                    cancel();
                else
                    launch();
            }
        }.runTaskTimer(Lobby.getInstance(), 20L, 20L);
    }

    private void launch() {
        if (this.owning == null || !this.owning.isOnline())
            return;
        Firework fw = (Firework) this.owning.getWorld().
                spawnEntity(this.owning.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random RANDOM = Commons.getRandom();

        int rt = RANDOM.nextInt(5);
        FireworkEffect.Type type = FireworkEffect.Type.values()[rt];

        Color c1 = Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
        Color c2 = Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));

        FireworkEffect effect = FireworkEffect.builder().flicker(RANDOM.nextBoolean())
                .withColor(c1).withFade(c2).with(type)
                .trail(RANDOM.nextBoolean()).build();

        fwm.addEffect(effect);

        fwm.setPower(RANDOM.nextInt(3));

        fw.setFireworkMeta(fwm);
    }

    @Override
    public void remove() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        removeGadgetFromOwningPlayer();
    }
}
