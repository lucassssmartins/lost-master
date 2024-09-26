package com.lostmc.pvp.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import org.bukkit.entity.Player;

public class SetWarpCommand extends WrappedBukkitCommand {

    public SetWarpCommand() {
        super("setwarp");
        this.setPermission("pvp.cmd.setwarp");
        this.setAliases("warpset", "set");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player player = (Player) sender.getHandle();
            if (args.length == 0) {
                sender.tlMessage("command.setwarp.usage");
            } else {
                WarpController controller = PvP.getControl().getController(WarpController.class);
                Warp warp = controller.searchWarp(args[0]);
                if (warp != null) {
                    warp.setLocation(player.getLocation());
                    controller.saveWarpLocation(warp);
                    sender.tlMessage("command.setwarp.success", warp.getName());
                } else {
                    sender.tlMessage("command.setwarp.warp-not-found", args[0]);
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
