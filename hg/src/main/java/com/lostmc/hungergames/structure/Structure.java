package com.lostmc.hungergames.structure;

import org.bukkit.Location;

public interface Structure {

	Location findPlace();

	void place(Location loc);
}
