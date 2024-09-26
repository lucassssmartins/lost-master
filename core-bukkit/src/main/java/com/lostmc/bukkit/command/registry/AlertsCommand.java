package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.entity.Player;

public class AlertsCommand extends WrappedBukkitCommand {

    public AlertsCommand() {
        super("alerts");
        setPermission("core.moderate.alerts");
        setAliases("ac");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Profile profile = Profile.getProfile(sender.getHandle());
            boolean toggle;
            profile.setData(DataType.AC_ALERTS, toggle = !profile.getData(DataType.AC_ALERTS).getAsBoolean());
            profile.save();
            sender.sendMessage("§eVocê §b" + (toggle ? "ativou" : "desativou") + " §eos alerts!");
        } else {
            sender.sendInGameMessage();
        }
    }
}
