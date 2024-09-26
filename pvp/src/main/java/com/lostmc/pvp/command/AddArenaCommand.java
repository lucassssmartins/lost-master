package com.lostmc.pvp.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import org.bukkit.entity.Player;

public class AddArenaCommand extends WrappedBukkitCommand {

    public AddArenaCommand() {
        super("addarena");
        setPermission("*");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            ArenaWarp arena = PvP.getControl().getController(WarpController.class)
                    .getWarpByClass(ArenaWarp.class);
            if (arena != null) {
                arena.addRandomTp(((Player) sender.getHandle()).getLocation());
                sender.sendMessage("§aArena added!");
            } else {
                sender.sendMessage("§cArena warp not found.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
