package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.tag.Tag;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FindCommand extends WrappedProxyCommand {

    public FindCommand() {
        super("find");
        setPermission("core.cmd.find");
        setAliases("finder", "go");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            if (args.length == 0) {
                sender.sendMessage("§cUso: /find <target>");
            } else {
                ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[0]);
                if (t != null) {
                    TextComponent playerMessage = new TextComponent(
                            Tag.fromRank(Profile.getProfile(t).getRank()).getPrefix() + t.getName());
                    TextComponent space = new TextComponent(" §f- ");
                    TextComponent ip = new TextComponent("§9" + t.getServer().getInfo().getName().toUpperCase());
                    ip.setClickEvent(
                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + t.getServer().getInfo().getName()));
                    ip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new TextComponent[] { new TextComponent("§9Clique para se conectar ate o servidor") }));
                    if (sender.getHandle() instanceof ProxiedPlayer)
                        ((ProxiedPlayer) sender.getHandle()).sendMessage(playerMessage, space, ip);
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
