package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StaffListCommand extends WrappedProxyCommand {

    public StaffListCommand() {
        super("stafflist");
        setPermission("core.cmd.stafflist");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        List<Profile> moderators = new ArrayList<>();
        for (Profile profile : Commons.getProfileMap().values()) {
            if (profile.getRank().ordinal() <= Rank.YTPLUS.ordinal()) {
                moderators.add(profile);
            }
        }
        if (moderators.isEmpty()) {
            sender.sendMessage("§cNão há nenhum staffer online no momento.");
        } else {
            moderators.sort(Comparator.comparing(o -> getSort(o.getRank())));
            int id = 1;
            for (Profile moderator : moderators) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(moderator.getUniqueId());

                String serverId = p != null ? p.getServer().getInfo().getName().toUpperCase()
                        : "NOT-FOUND";

                String nametag = Tag.fromRank(moderator.getRank()).getPrefix() + moderator.getName();

                String n = moderator.getData(DataType.NICKNAME).getAsString();
                String nickname = n.isEmpty() ? "" : "§e(§6" + n + "§e) - ";

                String format = "§a" + id + "° - " + nametag + " §e- " + nickname + "§f(§a" + serverId + "§f)";

                if (sender.getHandle() instanceof ProxiedPlayer) {
                    TextComponent text = new TextComponent(format);
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + serverId));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            TextComponent.fromLegacyText("§eClique para conectar no servidor")));
                    ((ProxiedPlayer) sender.getHandle()).sendMessage(text);
                } else {
                    sender.sendMessage(format);
                }
                ++id;
            }
            sender.sendMessage("§eNo momento há §b" + moderators.size() + "§e staffers online!");
        }
    }

    private String getSort(Rank rank) {
        String aphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return String.valueOf(aphabet.charAt(rank.ordinal()));
    }
}
