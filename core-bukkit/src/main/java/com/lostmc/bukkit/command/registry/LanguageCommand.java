package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.menu.LanguageMenu;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import org.bukkit.entity.Player;

public class LanguageCommand extends WrappedBukkitCommand {

    public LanguageCommand() {
        super("language");
        this.setAliases("lang", "idioma", "idiom");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            ((Player) sender.getHandle()).openInventory(
                    new LanguageMenu(Profile.getProfile(sender.getHandle()).getLocale()));
        } else {
            sender.sendInGameMessage();
        }
    }
}
