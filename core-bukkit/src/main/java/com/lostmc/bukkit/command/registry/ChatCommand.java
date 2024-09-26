package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.chat.ChatController;
import com.lostmc.bukkit.chat.ChatState;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;

public class ChatCommand extends WrappedBukkitCommand {

    public ChatCommand() {
        super("chat");
        setPermission("core.cmd.chat");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /chat <on, off, clear>");
        } else {
            ChatController controller = BukkitPlugin.getControl().getController(ChatController.class);
            if (args[0].equalsIgnoreCase("on")) {
                if (controller.getChatState() != ChatState.ENABLED) {
                    controller.setChatState(ChatState.ENABLED);
                    MessageAPI.sendAlert(sender.getName() + " habilitou o bate-papo");
                    sender.sendMessage("§aVocê habilitou o bate-papo.");
                    Bukkit.broadcastMessage("§aO bate-papo foi habilitado!");
                } else {
                    sender.sendMessage("§cO bate-papo já está habilitado!");
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                if (controller.getChatState() != ChatState.DISABLED) {
                    controller.setChatState(ChatState.DISABLED);
                    MessageAPI.sendAlert(sender.getName() + " desabilitou o bate-papo");
                    sender.sendMessage("§CVocê desabilitou o bate-papo.");
                    Bukkit.broadcastMessage("§cO bate-papo foi desabilitado!");
                } else {
                    sender.sendMessage("§cO bate-papo já está desabilitado!");
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                controller.clearChat(200);
                MessageAPI.sendAlert(sender.getName() + " limpou o bate-papo");
            }
        }
    }
}
