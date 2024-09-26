package com.lostmc.bukkit.api.actionbar;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.ProtocolLibrary;
import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ActionBarAPI {

	public static void send(Player player, String text) {
		int protocolVersion = Via.getAPI().getPlayerVersion(player);
		if (protocolVersion > 5) {
			PacketContainer chatPacket = new PacketContainer(PacketType.Play.Server.CHAT);
			chatPacket.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + " \"}"));
			chatPacket.getBytes().write(0, (byte) 2);
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, chatPacket);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Cannot send packet " + chatPacket, e);
			}
		}
	}
}
