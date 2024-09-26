package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.menu.PreferencesMenu;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import org.bukkit.entity.Player;

public class PreferencesCommand extends WrappedBukkitCommand {

    public PreferencesCommand() {
        super("preferences");
        this.setAliases("prefs");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            ((Player) sender.getHandle()).openInventory(
                    new PreferencesMenu(Profile.getProfile(sender.getHandle())));
        } else {
            sender.sendInGameMessage();
        }
    }
}
