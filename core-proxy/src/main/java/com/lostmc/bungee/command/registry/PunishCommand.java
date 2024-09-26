package com.lostmc.bungee.command.registry;

import com.google.common.collect.Lists;
import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.bungee.utils.ForceOP;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.punishment.BanPunishment;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.SilentPunishment;
import com.lostmc.core.punishment.provider.PunishmentProvider;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.UUID;

public class PunishCommand extends WrappedProxyCommand {

    public PunishCommand() {
        super("punish");
        this.setPermission("core.cmd.punish");
        this.setAliases("punir", "p");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUso: /punish <type> <category> n <nick> <reason>");
        } else {
            Punishment.Type type;
            try {
                type = Punishment.Type.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage("§cTipo de punição '" + args[0] + "' não encontrada.");
                return;
            }

            Punishment.Category category;
            try {
                category = Punishment.Category.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage("§cCategoria de punição '" + args[1] + "' não encontrada.");
                return;
            }

            if (category == Punishment.Category.BLACKLIST && !sender.hasPermission("core.admin.blacklist")) {
                sender.sendMessage("§cVocê não possui permissão para usar a categoria blacklist.");
                return;
            }

            if (category != Punishment.Category.COMMUNITY &&
                    (category.getType() == Punishment.Type.BAN
                            || category.getType() == Punishment.Type.IP_BAN)
                    && type == Punishment.Type.SILENT) {
                sender.sendMessage("§cA categoria " + category.toString().toLowerCase() + " não é " +
                        "compatível com o tipo " + type.toString().toLowerCase() + ".");
                return;
            }

            if (!args[2].equalsIgnoreCase("n")) {
                sender.sendMessage("§cUso incorreto: /punish <type> <category> n <nick> <reason>");
                return;
            }

            Profile profile = Commons.searchProfile(args[3]);
            if (profile == null) {
                sender.sendMessage("§aRegistrando conta (perfil não encontrado)...");

                UUID id = Commons.getUuidFetcher().getUUIDOf(args[0]);
                if (id == null)
                    id = Commons.getUuidFetcher().getFixedOfflineUUID(args[0]);
                try {
                    profile = Commons.getStorageCommon().getAccountStorage().createProfile(id, args[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cNão foi possível registrar a conta '" + args[3] + "'! Tente novamente mais tarde.");
                    return;
                }
            }

            if (ForceOP.isOP(profile.getUniqueId()) && type == Punishment.Type.BAN) {
                sender.sendMessage("§cVocê não pode punir este usuário!");
                return;
            }

            try {
                List<Punishment> current = Commons.getStorageCommon().getPunishmentStorage().getPlayerPunishments(profile.getUniqueId(), type);
                if (!current.isEmpty()) {
                    sender.sendMessage("§cEste jogador já possui uma punição do tipo " + type.toString().toLowerCase() + " ativa: " +
                            current.get(0));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível verificar por punições antigas do jogador: " + e);
                return;
            }

            String reason = createArgs(4, args);
            if (reason.isEmpty())
                reason = "não informado";
            Punishment punishment = null;
            Exception error = null;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(profile.getUniqueId());
            String serverId = player != null ? player.getServer().getInfo().getName() : "NRE";

            switch (type) {
                case BAN: {
                    try {
                        punishment = PunishmentProvider.provideNewBaniment(reason, serverId, new Punishment.PlayerData(profile.getUniqueId(),
                                        profile.getName()), new Punishment.PlayerData(sender.getHandle() instanceof ProxiedPlayer ?
                                        ((ProxiedPlayer) sender.getHandle()).getUniqueId() : Commons.CONSOLE_UNIQUEID, sender.getName()),
                                category, (category.getTimeFormat().equals("-1") ? -1 : DateUtils.parseDateDiff(category.getTimeFormat())), false, category.isPardonnable());
                    } catch (Exception e) {
                        error = e;
                    }
                    break;
                }
                case IP_BAN: {
                    String ipAddress = profile.getData(DataType.LAST_IP_ADDRESS).getAsString();
                    if (!ipAddress.isEmpty()) {
                        try {
                            punishment = PunishmentProvider.provideNewIPBaniment(reason, serverId, new Punishment.PlayerData(profile.getUniqueId(),
                                            profile.getName()), new Punishment.PlayerData(sender.getHandle() instanceof ProxiedPlayer ?
                                            ((ProxiedPlayer) sender.getHandle()).getUniqueId() : Commons.CONSOLE_UNIQUEID, sender.getName()),
                                    category, (category.getTimeFormat().equals("-1") ? -1 : DateUtils.parseDateDiff(category.getTimeFormat())), false, category.isPardonnable(), ipAddress);
                        } catch (Exception e) {
                            error = e;
                        }
                    }
                    break;
                }
                case SILENT: {
                    try {
                        punishment = PunishmentProvider.provideNewSilent(category.getDefaultReason(), serverId, new Punishment.PlayerData(profile.getUniqueId(),
                                        profile.getName()), new Punishment.PlayerData(sender.getHandle() instanceof ProxiedPlayer ?
                                        ((ProxiedPlayer) sender.getHandle()).getUniqueId() : Commons.CONSOLE_UNIQUEID, sender.getName()),
                                category, (category.getTimeFormat().equals("-1") ? -1 : DateUtils.parseDateDiff(category.getTimeFormat())));
                    } catch (Exception e) {
                        error = e;
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            if (error != null) {
                error.printStackTrace();
                sender.sendMessage("§cFalha ao gerar id da punição: " + error.getMessage());
                return;
            }

            if (punishment != null) {
                sender.sendMessage("§aProcessando punição...");

                try {
                    Commons.getStorageCommon().getPunishmentStorage().recordPunishment(punishment);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cFalha ao processar punição: " + e.getMessage());
                    return;
                }

                if (player != null) {
                    if (type == Punishment.Type.IP_BAN || type == Punishment.Type.BAN) {
                        player.disconnect(PunishmentProvider.provideBanimentKickMessage((BanPunishment) punishment));
                    } else if (type == Punishment.Type.SILENT) {
                        ProxyPlugin.getInstance().getSilentManager().silent(profile.getUniqueId(),
                                (SilentPunishment) punishment);
                        if (!((SilentPunishment) punishment).isLifetime())
                            player.sendMessage(new TextComponent("§cVocê foi mutado por " + punishment.getReason() + ", expira em "
                                    + DateUtils.getTime(((SilentPunishment) punishment).getExpiresIn())));
                        else {
                            player.sendMessage(new TextComponent("§cVocê foi mutado permanentemente por " + punishment.getReason()));
                        }
                    }
                }

                sender.sendMessage("§aVocê puniu o jogador " + profile.getName() + " por " + reason + " na categoria "
                        + category.toString().toLowerCase() + " com sucesso!");

                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (p.getServer().getInfo().getName().equals(serverId) && punishment instanceof BanPunishment &&
                            ((BanPunishment) punishment).getCategory() == Punishment.Category.CHEATING)
                        p.sendMessage(TextComponent.fromLegacyText("§cUm jogador utilizando trapaças em sua sala foi banido."));
                    if (!p.hasPermission("core.cmd.punish"))
                        continue;
                    p.sendMessage(TextComponent.fromLegacyText("§e" + profile.getName() + "(" + profile.getUniqueId() + ") " +
                            "foi punido por " + reason + " na categoria " + category.toString().toLowerCase() + " por " + sender.getName()));
                }
            } else {
                sender.sendMessage("§cPunição não processada pois o jogador não possui um endereço de ip registrado.");
            }
        }
    }

    @Override
    public List<String> complete(WrappedCommandSender sender, String[] args) {
        if (args.length == 1) {
            return getTypes(args[0]);
        } else if (args.length == 2) {
            return getCategories(args[0], args[1]);
        } else {
            return null;
        }
    }

    private String createArgs(int begin, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < args.length; i++)
            sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
        return sb.toString();
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

    public List<String> getCategories(String type, String args) {
        List<String> completions = Lists.newArrayList();
        if (!args.isEmpty()) {
            for (Punishment.Category category : Punishment.Category.values()) {
                if ((type.equalsIgnoreCase("ban") || type.equalsIgnoreCase("ipban"))
                        && category.getType() == Punishment.Type.BAN) {
                    if (StringUtils.startsWithIgnoreCase(category.toString(), args)) {
                        completions.add(category.toString().toLowerCase());
                    }
                } else if (type.equalsIgnoreCase("silent") && (category == Punishment.Category.COMMUNITY
                        || category.getType() == Punishment.Type.SILENT)) {
                    if (StringUtils.startsWithIgnoreCase(category.toString(), args)) {
                        completions.add(category.toString().toLowerCase());
                    }
                }
            }
        } else {
            for (Punishment.Category category : Punishment.Category.values()) {
                if ((type.equalsIgnoreCase("ban") || type.equalsIgnoreCase("ipban"))
                        && category.getType() == Punishment.Type.BAN) {
                    completions.add(category.toString().toLowerCase());
                } else if (type.equalsIgnoreCase("silent")
                        && (category == Punishment.Category.COMMUNITY
                        || category.getType() == Punishment.Type.SILENT)) {
                    completions.add(category.toString().toLowerCase());
                }
            }
        }
        return completions;
    }
}
