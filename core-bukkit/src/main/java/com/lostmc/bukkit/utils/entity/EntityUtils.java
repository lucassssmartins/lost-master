package com.lostmc.bukkit.utils.entity;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

public class EntityUtils {

	public static synchronized int next() {
		try {
			Class<?> clazz = net.minecraft.server.v1_8_R3.Entity.class;
			Field field = clazz.getDeclaredField("entityCount");
			field.setAccessible(true);
			int id = field.getInt(null);
			field.set(null, id + 1);
			return id;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int clearDrops() {
		int drops = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Item) {
					entity.remove();
					++drops;
				}
			}
		}
		return drops;
	}

	public static int clearEntities() {
		int entities = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Item || entity instanceof Animals || entity instanceof Monster) {
					entity.remove();
					++entities;
				}
			}
		}
		return entities;
	}
}
