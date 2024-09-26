package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.worldedit.AsyncWorldEdit;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArenaCommand extends WrappedBukkitCommand {

    public ArenaCommand() {
        super("arena");
        setPermission("hg.cmd.arena");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length <= 1) {
                p.sendMessage("§cUso: /arena <range> <height>");
            } else {
                int range;
                try {
                    range = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    p.sendMessage("§cValor do raio inválido: " + e);
                    return;
                }
                int height;
                try {
                    height = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    p.sendMessage("§cValor da altura inválida: " + e);
                    return;
                }
                if (height <= 0 || height > 60) {
                    p.sendMessage("§cA altura precisa estar entre 1 e 60.");
                    return;
                }
                if (range > 60) {
                    p.sendMessage("§cO raio não pode ser maior que 60.");
                    return;
                }
                p.sendMessage("§eGerando arena...");
                generateArena(p.getLocation(), range, height);
                p.sendMessage("§eGerada!");
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public void generateArena(Location location, int size, int height) {
        AsyncWorldEdit asyncWorldEdit = AsyncWorldEdit.getInstance();
        for (int y = 0; y <= height; y++) {
            if (y == 0 || y == height) {
                for (int x = -size; x <= size; x++) {
                    for (int z = -size; z <= size; z++) {
                        Location loc = location.clone().add(x, y, z);
                        asyncWorldEdit.setAsyncBlock(location.getWorld(), loc.getBlockX(),
                                loc.getBlockY(), loc.getBlockZ(), Material.BEDROCK.getId(), (byte) 0);
                    }
                }
            } else {
                for (int x = -size; x <= size; x++) {
                    Location loc = location.clone().add(x, y, size);
                    Location loc2 = location.clone().add(x, y, -size);
                    asyncWorldEdit.setAsyncBlock(location.getWorld(), loc.getBlockX(),
                            loc.getBlockY(), loc.getBlockZ(), Material.BEDROCK.getId(), (byte) 0);
                    asyncWorldEdit.setAsyncBlock(location.getWorld(), loc2.getBlockX(),
                            loc2.getBlockY(), loc2.getBlockZ(), Material.BEDROCK.getId(), (byte) 0);
                }
                for (int z = -size; z <= size; z++) {
                    Location loc = location.clone().add(size, y, z);
                    Location loc2 = location.clone().add(-size, y, z);
                    asyncWorldEdit.setAsyncBlock(location.getWorld(), loc.getBlockX(),
                            loc.getBlockY(), loc.getBlockZ(), Material.BEDROCK.getId(), (byte) 0);
                    asyncWorldEdit.setAsyncBlock(location.getWorld(), loc2.getBlockX(),
                            loc2.getBlockY(), loc2.getBlockZ(), Material.BEDROCK.getId(), (byte) 0);
                }
            }
        }
    }
}
