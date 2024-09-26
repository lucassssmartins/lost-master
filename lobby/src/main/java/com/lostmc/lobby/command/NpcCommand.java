package com.lostmc.lobby.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.translate.Translator;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.npc.NonPlayerController;
import org.bukkit.entity.Player;

public class NpcCommand extends WrappedBukkitCommand {

    public NpcCommand() {
        super("npc");
        this.setPermission("hub.cmd.npc");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(Translator.tl(sender.getLocale(), "hub.command.npc.usage"));
            } else {
                try {
                    NonPlayerController.NonPlayerType type =
                            NonPlayerController.NonPlayerType.valueOf(args[0].toUpperCase());
                    Lobby.getControl().getController(NonPlayerController.class)
                            .saveLocation(((Player) sender.getHandle()).getLocation(), type);
                    sender.sendMessage(Translator.tl(sender.getLocale(), "hub.command.npc.success",
                            type.toString().toLowerCase()));
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Translator.tl(sender.getLocale(), "hub.command.npc.unknow-nonplayer-type"));
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
