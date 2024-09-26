package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReplyCommand extends WrappedBukkitCommand {

    public ReplyCommand() {
        super("reply");
        setAliases("responder", "r");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§c Uso: /r <message>");
            } else {
                if (MessageCommand.lastTellUUID.containsKey(p.getUniqueId())) {
                    Player t = Bukkit.getPlayer(MessageCommand.lastTellUUID.get(p.getUniqueId()));
                    Profile tP = Profile.getProfile(t);
                    if (t != null) {
                        if (tP.getData(DataType.PRIVATE_MESSAGES).getAsBoolean()) {
                            String message = MessageCommand.createArgs(0, args);
                            MessageCommand.lastTellUUID.put(t.getUniqueId(), p.getUniqueId());
                            MessageCommand.lastTellUUID.put(p.getUniqueId(), t.getUniqueId());
                            t.sendMessage("§7[Mensagem de §f" + p.getName() + "§7] " + message);
                            p.sendMessage("§7[Mensagem para §f" + t.getName() + "§7] " + message);
                        } else {
                            p.sendMessage(
                                    "§cEste jogador desativou as mensagens privadas.");
                        }
                    } else {
                        p.sendMessage("§cO último jogador com quem conversou não foi encontrado.");
                    }
                } else {
                    p.sendMessage("§cVocê não possui ninguém para responder.");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
