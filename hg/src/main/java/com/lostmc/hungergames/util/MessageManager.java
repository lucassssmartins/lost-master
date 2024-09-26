package com.lostmc.hungergames.util;

import java.util.Random;

import com.lostmc.core.utils.DateUtils;
import org.bukkit.Location;

public class MessageManager {

	public static String getPlayersMessage(int time) {
		return "§cO torneio iniciará em " + DateUtils.formatDifference(time);
	}

	public static String getCountDownMessage(CountDownMessageType type, int time) {
		return getCountDownMessage(type, time, null);
	}

	public static String getCountDownMessage(CountDownMessageType type, int time, Location loc) {
		switch (type) {
			case GAMESTART: {
				return "§cO torneio iniciará em " + DateUtils.formatDifference(time);
			}
			case FEAST: {
				return "§cO feast irá spawnar em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
						") em " + DateUtils.formatDifference(time);
			}
			case FINISH: {
				return "§cO combate final acontecerá em " + DateUtils.formatDifference(time);
			}
			case FINALARENA: {
				return "§cA arena final irá acontecer em " + DateUtils.formatDifference(time);
			}
			case FEAST_SPAWNED: {
				return "§cO feast spawnou em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
						")";
			}
			case INVINCIBILITY: {
				return "§cA invencibilidade acabará em " + DateUtils.formatDifference(time);
			}
		}
		return "";
	}

	public static String getMinifeastMessage(Location loc) {
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		while (x % 50 != 0) {
			x++;
		}
		while (z % 50 != 0) {
			z++;
		}
		int x2 = x;
		int z2 = z;
		Random r = new Random();
		if (r.nextBoolean()) {
			x += 50;
			x2 -= 50;
		} else {
			x -= 50;
			x2 += 50;
		}

		if (r.nextBoolean()) {
			z += 50;
			z2 -= 50;
		} else {
			z -= 50;
			z2 += 50;
		}
		return "§cUm Mini feast spawnou por volta de [X: " + x + " e Z: " + z + "] e [X: " + x2 + " e Z: " + z2 + "]";
	}

	public enum CountDownMessageType {
		GAMESTART, INVINCIBILITY, FEAST, FEAST_SPAWNED, FINALARENA, FINISH;
	}
}
