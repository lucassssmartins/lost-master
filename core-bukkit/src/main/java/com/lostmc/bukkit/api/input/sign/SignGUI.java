package com.lostmc.bukkit.api.input.sign;

import com.lostmc.bukkit.BukkitPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SignGUI {

    private final static SignGUI instance = new SignGUI();
    protected PacketAdapter packetAdapter;

    protected ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    protected Map<UUID, SignGUIListener> listeners = new ConcurrentHashMap<>();
    protected Map<UUID, Location> locations = new ConcurrentHashMap<>();

    private SignGUI() {
        protocolManager.addPacketListener(packetAdapter =
                new PacketAdapter(BukkitPlugin.getInstance(), ListenerPriority.HIGHEST,
                        PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Object realPacket = event.getPacket().getHandle();
                if (realPacket instanceof PacketPlayInUpdateSign) {
                    Player player = event.getPlayer();
                    UUID playerUUID = player.getUniqueId();

                    IChatBaseComponent[] c = ((PacketPlayInUpdateSign) realPacket).b();
                    String[] conversion = new String[c.length];
                    for (int i = 0; i < conversion.length; i++) {
                        IChatBaseComponent next = c[i];
                        conversion[i] = next != null ? next.getText() : null;
                    }

                    SignGUIListener signInputListener = listeners.remove(playerUUID);
                    if (signInputListener != null) {
                        event.setCancelled(true);
                        signInputListener.onSignDone(player, conversion);
                    }
                }
            }
        });
    }

    public void openSign(Player player, SignGUIListener listener) {
        BlockPosition signLocation = new BlockPosition(player.getLocation().getBlockX(), 0, player.getLocation().getBlockZ());
        player.sendBlockChange(player.getLocation().clone().subtract(0, 2, 0), 68, (byte) 0);
        PacketContainer openSignEditor = protocolManager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        openSignEditor.getBlockPositionModifier().write(0, signLocation);
        try {
            protocolManager.sendServerPacket(player, openSignEditor);
            locations.put(player.getUniqueId(), signLocation.toLocation(player.getWorld()));
            listeners.put(player.getUniqueId(), listener);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void destroy() {
        protocolManager.removePacketListener(packetAdapter);
        locations.clear();
        listeners.clear();
    }

    public static SignGUI getInstance() {
        return instance;
    }
}
