package com.lostmc.bukkit.api.title;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class TitleAPI {

	public static void setTitle(Player player, String title, String subtitle, int fadeInTime, int stayTime,
			int fadeOutTime, boolean ticks) {
		if (fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1)
			setTimes(player, fadeInTime, stayTime, fadeOutTime, ticks);
		if (title != null && !title.isEmpty())
			setTitle(player, title);
		if (subtitle != null && !subtitle.isEmpty())
			setSubtitle(player, subtitle);
	}

	public static void setTitle(Player player, String title, String subtitle, int fadeInTime, int stayTime,
			int fadeOutTime) {
		if (fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1)
			setTimes(player, fadeInTime, stayTime, fadeOutTime, true);
		if (title != null && !title.isEmpty())
			setTitle(player, title);
		if (subtitle != null && !subtitle.isEmpty())
			setSubtitle(player, subtitle);
	}

	public static void setTitle(Player player, String title, String subtitle) {
		if (title != null && !title.isEmpty())
			setTitle(player, title);
		if (subtitle != null && !subtitle.isEmpty())
			setSubtitle(player, subtitle);
	}

	public static void setTitle(Player player, String string) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, TitleAction.TITLE);
		packet.getChatComponents().write(0, WrappedChatComponent.fromText(string));
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void setSubtitle(Player player, String string) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, TitleAction.SUBTITLE);
		packet.getChatComponents().write(0, WrappedChatComponent.fromText(string));
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void setTimes(Player player, int fadeInTime, int stayTime, int fadeOutTime, boolean ticks) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, TitleAction.TIMES);
		packet.getIntegers().write(0, fadeInTime * (ticks ? 1 : 20));
		packet.getIntegers().write(1, stayTime * (ticks ? 1 : 20));
		packet.getIntegers().write(2, fadeOutTime * (ticks ? 1 : 20));
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void resetTitle(Player player) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, TitleAction.RESET);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void clearTitle(Player player) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, TitleAction.CLEAR);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
