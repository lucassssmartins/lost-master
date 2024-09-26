package com.lostmc.bungee.command.registry;

import com.google.common.collect.Lists;
import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.SilentPunishment;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.StringUtils;

import javax.xml.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PardonCommand extends WrappedProxyCommand {

    public PardonCommand() {
        super("pardon");
        this.setPermission("core.cmd.pardon");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /pardon <punishment/player> <punishment id/nick> <type>");
        } else {
            if (args[0].equalsIgnoreCase("punishment")) {
                String id = args[1];
                Punishment punishment = null;
                try {
                    punishment = Commons.getStorageCommon().getPunishmentStorage().getPunishment(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cFalha ao carregar punição #" + id);
                }

                if (punishment != null) {
                    sender.sendMessage("§aRevogando punição...");
                    try {
                        Commons.getStorageCommon().getPunishmentStorage().pardonPunishment(id);
                        if (punishment instanceof SilentPunishment)
                            ProxyPlugin.getInstance().getSilentManager().removeSilent(punishment
                                    .getPlayerData().getUniqueId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage("§cFalha na revogação da punição #" + id);
                        return;
                    }

                    sender.sendMessage("§aPunição #" + id + " revogada com sucesso!");

                    Punishment.PlayerData playerData = punishment.getPlayerData();
                    for (ProxiedPlayer o : ProxyServer.getInstance().getPlayers()) {
                        if (!o.hasPermission("core.cmd.pardon"))
                            continue;
                        o.sendMessage(TextComponent.fromLegacyText("§e" + playerData.getName() + "(" + playerData.getUniqueId() +
                                ") teve a punição do tipo " + punishment.getType().toString().toLowerCase() + " revogada por "
                                + sender.getName()));
                    }
                } else {
                    sender.sendMessage("§cPunição #" + id + " não encontrada.");
                }
            } else if (args[0].equalsIgnoreCase("player") && args.length >= 3) {
                Punishment.Type type;
                try {
                    type = Punishment.Type.valueOf(args[2].toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage("§cPunição do tipo '" + args[2] + "' não encontrada.");
                    return;
                }
                Profile profile = Commons.searchProfile(args[1]);
                if (profile != null) {
                    List<Punishment> punishments;
                    try {
                        punishments = Commons.getStorageCommon().getPunishmentStorage()
                                .getPlayerPunishments(profile.getUniqueId(), type);
                    } catch (Exception e) {
                        sender.sendMessage("§cNão foi possível carregar punições ativas para " + profile.getName() + ": " + e);
                        return;
                    }
                    if (!punishments.isEmpty()) {
                        for (Punishment punishment : punishments) {
                            try {
                                sender.sendMessage("§aRevogando punição #" + punishment.getId() + "...");
                                Commons.getStorageCommon().getPunishmentStorage().pardonPunishment(punishment.getId());
                                if (punishment instanceof SilentPunishment)
                                    ProxyPlugin.getInstance().getSilentManager().removeSilent(punishment
                                            .getPlayerData().getUniqueId());
                            } catch (Exception e) {
                                e.printStackTrace();
                                sender.sendMessage("§cFalha na revogação da punição #" + punishment.getId() + "!");
                                return;
                            }
                        }
                        sender.sendMessage("§aOperação concluída com êxito!");
                        for (ProxiedPlayer o : ProxyServer.getInstance().getPlayers()) {
                            if (!o.hasPermission("core.cmd.pardon"))
                                continue;
                            o.sendMessage(TextComponent.fromLegacyText("§e" + profile.getName() + "(" + profile.getUniqueId() +
                                    ") teve a punição do tipo " + type.toString().toLowerCase() + " revogada por " + sender.getName()));
                        }
                    } else {
                        sender.sendMessage("§cNenhuma punição do tipo " + type.toString().toLowerCase() + " ativa encontrada para "
                                + profile.getName());
                    }
                } else {
                    sender.sendMessage("§cPerfil não encontrado para " + args[1]);
                }
            } else {
                sender.sendMessage("§cUso: /pardon <punishment/player> <punishment id/nick> <type>");
            }
        }
    }

    @Override
    public List<String> complete(WrappedCommandSender sender, String[] args) {
        if (args.length == 1)  {
            return getTags(args[0]);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("player")){
            return getTypes(args[2]);
        } else {
            return null;
        }
    }

    public List<String> getTags(String args) {
        List<String> completions = Lists.newArrayList();
        if (!args.isEmpty()) {
            for (String tag : new String[] { "player", "punishment" }) {
                if (StringUtils.startsWithIgnoreCase(tag, args)) {
                    completions.add(tag);
                }
            }
        } else {
            completions.addAll(Arrays.asList("player", "punishment"));
        }
        return completions;
    }

    public List<String> getTypes(String args) {
        List<String> completions = Lists.newArrayList();
        if (!args.isEmpty()) {
            for (Punishment.Type type : Punishment.Type.values()) {
                if (StringUtils.startsWithIgnoreCase(type.toString(), args)) {
                    completions.add(type.toString().toLowerCase());
                }
            }
        } else {
            for (Punishment.Type type : Punishment.Type.values()) {
                completions.add(type.toString().toLowerCase());
            }
        }
        return completions;
    }
}
