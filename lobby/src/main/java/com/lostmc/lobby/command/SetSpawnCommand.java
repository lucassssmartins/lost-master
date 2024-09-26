package com.lostmc.lobby.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.controller.LobbyController;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends WrappedBukkitCommand {

    public SetSpawnCommand() {
        super("setspawn");
        this.setPermission("pvp.cmd.setspawn");
        this.setAliases("setworldspawn");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Lobby.getControl().getController(LobbyController.class).saveSpawnLocation(
                    ((Player) sender.getHandle()).getLocation());
            sender.sendMessage("§aSpawn location saved.");
        } else {
            sender.sendInGameMessage();
        }
    }
}
