package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Properties;

public class MaxPlayersCommand extends WrappedBukkitCommand {

    public MaxPlayersCommand() {
        super("maxplayers");
        setPermission("core.cmd.maxplayers");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /maxplayers <amount>");
        } else {
            int maxPlayers;
            try {
                maxPlayers = Integer.parseInt(args[0]);
            } catch (Exception e) {
                sender.sendMessage("§cError: " + e);
                return;
            }

            try {
                setMaxPlayers(maxPlayers);
                sender.sendMessage("§aCapacidade máxima do servidor alterada para " + maxPlayers + ".");
            } catch (Exception e) {
                sender.sendMessage("§cFalha na alteração da capacidade máxima do servidor: " + e);
            }
        }
    }

    private void setMaxPlayers(int input) throws Exception {
        Server server = Bukkit.getServer();
        Field console = server.getClass().getDeclaredField("console");
        console.setAccessible(true);
        Object m = console.get(server);
        Field v = m.getClass().getSuperclass().getDeclaredField("v");
        v.setAccessible(true);
        Object p = v.get(m);
        Field maxPlayers = p.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxPlayers.setAccessible(true);
        maxPlayers.set(p, input);
        File file = new File(server.getWorldContainer(), "server.properties");
        Properties prop = new Properties();
        prop.load(new FileReader(file));
        prop.setProperty("max-players", String.valueOf(input));
        prop.store(new FileOutputStream(file), null);
    }
}
