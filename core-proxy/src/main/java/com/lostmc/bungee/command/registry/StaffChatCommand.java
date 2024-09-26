package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffChatCommand extends WrappedProxyCommand {

    public StaffChatCommand() {
        super("staffchat");
        setPermission("core.cmd.staffchat");
        setAliases("sc", "s");
    }

    @Override
    public synchronized void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            Profile profile = Profile.getProfile(p);
            if (args.length == 0) {
                profile.setData(DataType.STAFF_CHAT_MODE, !profile.getData(DataType.STAFF_CHAT_MODE).getAsBoolean());
                profile.save();
                Commons.getRedisBackend().saveRedisProfile(profile);
                sender.sendMessage(profile.getData(DataType.STAFF_CHAT_MODE).getAsBoolean() ?
                        "§aVocê entrou no bate-papo da equipe." : "§cVocê saiu do bate-papo da equipe.");
            } else if (args[0].equalsIgnoreCase("toggle")) {
                profile.setData(DataType.STAFF_CHAT_MESSAGES, !profile.getData(DataType.STAFF_CHAT_MESSAGES).getAsBoolean());
                profile.save();
                Commons.getRedisBackend().saveRedisProfile(profile);
                sender.sendMessage(profile.getData(DataType.STAFF_CHAT_MESSAGES).getAsBoolean() ?
                        "§aVocê ativou as mensagens do bate-papo da equipe." :
                        "§cVocê desativou as mensagens do bate-papo da equipe.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
