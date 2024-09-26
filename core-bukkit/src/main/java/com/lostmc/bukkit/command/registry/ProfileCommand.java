package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.menu.MyProfileMenu;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import org.bukkit.entity.Player;

public class ProfileCommand extends WrappedBukkitCommand {

    public ProfileCommand() {
        super("profile");
        setAliases("perfil");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            ((Player) sender.getHandle()).openInventory(new MyProfileMenu((Player) sender.getHandle(),
                    Profile.getProfile(sender.getHandle())));
        } else {
            sender.sendInGameMessage();
        }
    }
}
