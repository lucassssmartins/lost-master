package com.lostmc.hungergames.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lostmc.hungergames.structure.items.FeastType;
import com.lostmc.hungergames.structure.items.ItemGenerator;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class FeastChestStructure implements Structure {

	private static Random r = new Random();
	@Getter
	private FeastType type;

	public FeastChestStructure(FeastType type) {
		this.type = type;
	}

	@Override
	public Location findPlace() {
		return null;
	}

	@Override
	public void place(Location central) {
		List<Chest> chests = new ArrayList<>();
		central.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
					Location loc = central.clone().add(x, 1, z);
					loc.getBlock().setType(Material.CHEST);
					if (loc.getBlock().getType() == Material.CHEST) {
						Block b = loc.getBlock();
						if (b.getState() instanceof Chest)
							chests.add((Chest) loc.getBlock().getState());
					}
				}
			}
		}
		for (Chest chest : chests) {
			ItemGenerator.generateToChest(chest, type);
		}
	}
}
