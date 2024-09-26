package com.lostmc.lobby.menu;

import com.lostmc.bukkit.api.menu.MenuInventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public abstract class MenuUpdateHandler {

    protected MenuInventory menu;

    public abstract void onUpdate(Player p);

    @Override
    protected void finalize() throws Throwable {
        this.menu = null;
        super.finalize();
    }
}
