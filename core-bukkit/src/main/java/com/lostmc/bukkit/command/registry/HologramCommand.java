package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.leaderboard.HologramTopController;
import com.lostmc.bukkit.leaderboard.HologramTopType;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.entity.Player;

public class HologramCommand extends WrappedBukkitCommand {

    public HologramCommand() {
        super("hologram");
        setPermission("*");
        setAliases("hl");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUso: /hologram <top_1v1_elo/top_hg_elo/top_hg_kills/top_hg_wins/top_coins>");
            } else {
                HologramTopType topType;
                try {
                    topType = HologramTopType.valueOf(args[0].toUpperCase());
                } catch (Exception e) {
                    p.sendMessage("§cHologram type not found.");
                    return;
                }
                BukkitPlugin.getControl().getController(HologramTopController.class).saveLocation(topType,
                        p.getLocation());
                p.sendMessage("§aDone.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
