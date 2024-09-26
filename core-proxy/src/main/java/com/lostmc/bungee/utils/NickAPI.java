package com.lostmc.bungee.utils;

import com.lostmc.core.profile.Profile;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.util.Map;

public class NickAPI {

    public static void changePlayerName(ProxiedPlayer p, String name) {
        try {
            BungeeCord bungee = BungeeCord.getInstance();
            Map<String, UserConnection> connections = (Map<String, UserConnection>) new FieldResolver(bungee.getClass())
                    .resolve("connections").get(bungee);
            if (!name.isEmpty()) {
                connections.remove(p.getName());
                connections.put(name, (UserConnection) p);
            } else {
                connections.remove(name);
                connections.put(Profile.getProfile(p).getName(), (UserConnection) p);
            }
            AccessUtil.setAccessible(new FieldResolver(p.getClass()).resolve("name"))
                    .set(p, name);
            AccessUtil.setAccessible(new FieldResolver(p.getPendingConnection().getClass()).resolve("name"))
                    .set(p.getPendingConnection(), name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
