package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.menu.ReportMenu;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import org.bukkit.entity.Player;

public class ReportsCommand extends WrappedBukkitCommand {

    public ReportsCommand() {
        super("reports");
        this.setPermission("core.cmd.reports");
        this.setAliases("reportinv", "reportmenu");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile opener = Profile.getProfile(p);
            ((Player) sender.getHandle()).openInventory(new ReportMenu(opener, p, 1));
        } else {
            sender.sendInGameMessage();
        }
    }
}
