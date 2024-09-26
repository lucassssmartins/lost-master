package com.lostmc.bukkit.api.input.sign;

import java.util.ArrayList;
import java.util.List;

import com.lostmc.bukkit.utils.string.StringLoreUtils;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateSign;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SignInputGui {

	private String[] lines;

	public SignInputGui(String str) {
		List<String> linesList = new ArrayList<>();
		linesList.add("");
		for (String s : StringLoreUtils.getLore(12, str)) {
			if (linesList.size() < 4) {
				linesList.add(s);
			}
		}
		while (linesList.size() < 4) {
			linesList.add("");
		}
		lines = new String[linesList.size()];
		lines = linesList.toArray(lines);
	}

	@SuppressWarnings("deprecation")
	public void open(Player p) {
		Location signLocation = p.getLocation().clone().subtract(0, 2, 0);
		IChatBaseComponent[] chat = new IChatBaseComponent[lines.length];
		for (int i = 0; i < chat.length; i++)
			chat[i] = IChatBaseComponent.ChatSerializer.a(lines[i]);
		PacketPlayOutUpdateSign updateSign = new PacketPlayOutUpdateSign(
				((CraftWorld) signLocation.getWorld()).getHandle(), new BlockPosition(signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ()), chat);
		PacketPlayOutOpenSignEditor signEditor = new PacketPlayOutOpenSignEditor(new BlockPosition(signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ()));
		p.sendBlockChange(signLocation, 68, (byte) 0);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(updateSign);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(signEditor);
		p.sendBlockChange(signLocation, signLocation.getBlock().getTypeId(), signLocation.getBlock().getData());
	}

}
