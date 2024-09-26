package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.leaderboard.NpcTopController;
import com.lostmc.bukkit.leaderboard.NpcTopType;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.entity.Player;

public class Top3Command extends WrappedBukkitCommand {

    public Top3Command() {
        super("top3");
        this.setPermission("*");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length <= 1) {
                p.sendMessage("§cUso: /top3 <pvp_1v1_elo/hg_elo/hg_wins/hg_kills> <1/2/3>");
            } else {
                NpcTopType topType;
                try {
                    topType = NpcTopType.valueOf(args[0].toUpperCase());
                } catch (Exception e) {
                    p.sendMessage("§cNPC type not found.");
                    return;
                }
                Integer i;
                try {
                    i = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("§cInvalid integer!");
                    return;
                }
                if (i <= 0 || i > 3) {
                    sender.sendMessage("§cSize must be between 1 and 3");
                    return;
                }
                BukkitPlugin.getControl().getController(NpcTopController.class).saveLocation(topType,
                        p.getLocation(), i);
                p.sendMessage("§aDone.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
