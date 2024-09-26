package com.lostmc.game.constructor;

import com.lostmc.bukkit.api.cooldown.CooldownAPI;
import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import com.lostmc.bukkit.api.cooldown.types.ItemCooldown;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import com.lostmc.game.GamePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

@Getter
public abstract class Kit implements Listener {

    private GamePlugin plugin;
    private String name;
    @Setter
    private String description = "Kit sem descrição.";
    private String[] incompatibleKits;
    @Setter
    private long cooldown;
    @Setter
    private Material iconMaterial;
    private ItemStack[] items;
    @Setter
    private boolean free, actived = true;
    @Setter
    private int rentPrice, buyPrice;

    public Kit(GamePlugin plugin) {
        this.plugin = plugin;
        this.name = getClass().getSimpleName();
    }

    public void setIncompatibleKits(String... incompatibleKits) {
        this.incompatibleKits = incompatibleKits;
    }

    public boolean hasIncompatibleKits() {
        return this.incompatibleKits != null;
    }

    public boolean isIncompatible(Kit kit) {
        return hasIncompatibleKits() && Stream.of(this.incompatibleKits).filter(name -> name.equalsIgnoreCase(kit.getName()))
                .findFirst().orElse(null) != null;
    }

    public void setItems(ItemStack... items) {
        this.items = items;
    }

    public boolean isKitItem(ItemStack item) {
        if (items != null) {
            for (ItemStack i : items) {
                if (i.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMainItem(ItemStack item) {
        if (item == null || getMainItem() == null)
            return false;
        return getMainItem().equals(item);
    }

    public boolean isUsing(Player p) {
        if (!canUse(p))
            return false;
        Gamer gamer = Profile.getProfile(p).getResource(Gamer.class);
        return gamer.getKits().values().contains(this);
    }

    public abstract boolean canUse(Player player);

    public void putInCooldown(Player player) {
        if (this.cooldown > 0) {
            removeCooldown(player);
            CooldownAPI.addCooldown(player,
                    getMainItem() != null ? new ItemCooldown(getMainItem(), getName(), this.cooldown)
                            : new Cooldown(getName(), this.cooldown));
        }
    }

    public void removeCooldown(Player player) {
        CooldownAPI.removeCooldown(player, getName());
    }

    public boolean isInCooldown(Player player) {
        return CooldownAPI.getCooldown(player, getName()) != null;
    }

    public void sendCooldownMessage(Player player) {
        player.sendMessage(Translator.tl(Profile.getProfile(player).getLocale(), "pvp.kit-in-cooldown",
                CooldownAPI.getCooldownFormat(player, getName()), getName()));
    }

    public abstract ItemStack getMainItem();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Kit) {
            Kit that = (Kit) o;
            return that.name.equalsIgnoreCase(this.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
