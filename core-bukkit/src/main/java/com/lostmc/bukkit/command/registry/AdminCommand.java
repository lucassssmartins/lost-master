package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.entity.Player;

public class AdminCommand extends WrappedBukkitCommand {

    public AdminCommand() {
        super("admin");
        this.setPermission("core.cmd.admin");
        this.setAliases("v", "vanish");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            VanishController v = BukkitPlugin.getControl().getController(VanishController.class);
            if (v.isVanished(p)) {
                v.toggleVanish(p, PlayerVanishModeEvent.Mode.PLAYER);
            } else {
                v.toggleVanish(p, PlayerVanishModeEvent.Mode.VANISH);
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
