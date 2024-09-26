package com.lostmc.lobby.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.lobby.gamer.Gamer;
import org.bukkit.entity.Player;

public class FlightCommand extends WrappedBukkitCommand {

    public FlightCommand() {
        super("flight");
        this.setPermission("hub.cmd.flight");
        this.setAliases("fly", "voar");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile profile = Profile.getProfile(p);
            Gamer gamer = profile.getResource(Gamer.class);
            gamer.setFlying(!gamer.isFlying());
        } else {
            sender.sendInGameMessage();
        }
    }
}
