package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.worldedit.AsyncWorldEdit;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldEditCommand extends WrappedBukkitCommand {

    private AsyncWorldEdit asyncWorldEdit = AsyncWorldEdit.getInstance();

    public WorldEditCommand() {
        super("worldedit");
        setPermission("core.cmd.worldedit");
        setAliases("we");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player player = (Player) sender.getHandle();
            if (args.length == 0) {
                player.sendMessage("§cUso: /we <id> or /we <id:data>");
            } else {
                if (asyncWorldEdit.hasFirstPosition(player) && asyncWorldEdit.hasSecondPosition(player)) {
                    List<Location> areaToModify = asyncWorldEdit.fromTwoPoints(asyncWorldEdit.getPosition1(player),
                            asyncWorldEdit.getPosition2(player));

                    for (Location blockLocation : areaToModify) {
                        try {
                            asyncWorldEdit.setAsyncBlock(player.getWorld(), blockLocation, args[0]);
                        } catch (Exception ex) {
                            player.sendMessage("§c" + ex);
                            return;
                        }
                    }

                    player.sendMessage("§cBlocos modificados para "
                            + Material.getMaterial(Integer.valueOf(args[0].split(":")[0])).name().toLowerCase() + ".");
                    MessageAPI.sendAlert(player.getName() + " modificou blocos de uma área para "
                            + Material.getMaterial(Integer.valueOf(args[0].split(":")[0])).name().toLowerCase());
                } else {
                    player.sendMessage(
                            "§cVocê precisa selecionar as áreas 1 e 2 com um machado de madeira.");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
