package com.lostmc.lobby.collectables.gadget;

import com.lostmc.core.profile.Profile;
import com.lostmc.lobby.gamer.Gamer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Gadget {

    protected final Player owning;

    public Gadget(final Player owning) {
        this.owning = owning;
    }

    public abstract void spawn(Location where);

    public abstract void remove();

    protected void addGadgetToOwningPlayer() {
        if (this.owning == null || !this.owning.isOnline())
            return;
        Profile profile = Profile.getProfile(this.owning);
        if (profile == null)
            return;
        Gamer gamer = profile.getResource(Gamer.class);
        if (gamer.getGadgets().contains(this))
            return;
        gamer.getGadgets().add(this);
    }

    protected void removeGadgetFromOwningPlayer() {
        if (this.owning == null || !this.owning.isOnline())
            return;
        Profile profile = Profile.getProfile(this.owning);
        if (profile == null)
            return;
        Gamer gamer = profile.getResource(Gamer.class);
        gamer.getGadgets().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }
}
