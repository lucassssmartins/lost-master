package com.lostmc.bukkit.api.input.sign;

import com.lostmc.bukkit.BukkitPlugin;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

class SignInputPacketListener extends PacketHandler {

	private SignInputManager searchManager;

	public SignInputPacketListener(SignInputManager manager) {
		super(BukkitPlugin.getInstance());
		this.searchManager = manager;
	}

	@Override
	public void onReceive(ReceivedPacket packet) {
		if (packet.getPacket() instanceof PacketPlayInUpdateSign) {
			IChatBaseComponent[] c = ((PacketPlayInUpdateSign) packet.getPacket()).b();
			String[] conversion = new String[c.length];
			for (int i = 0; i < conversion.length; i++) {
				IChatBaseComponent next = c[i];
				conversion[i] = next != null ? next.getText() : null;
			}
			packet.setCancelled(searchManager.handle(packet.getPlayer(), conversion));
		}
	}

	@Override
	public void onSend(SentPacket packet) {

	}
}
