package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.bungee.utils.ForceOP;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.networking.PacketOutAddRank;
import com.lostmc.core.networking.PacketOutRemoveRank;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.translate.Translator;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountRankCommand extends WrappedProxyCommand {

    public AccountRankCommand() {
        super("accountrank");
        this.setPermission("core.cmd.accountrank");
        this.setAliases("groupset", "setgroup");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 2) {
            sender.tlMessage("command.accountrank.usage");
        } else if (args.length >= 3) {
           Profile profile = Commons.searchProfile(args[0]);
            if (args[1].equalsIgnoreCase("add")) {
                Rank rank;

                try {
                    rank = Rank.valueOf(Tag.fromName(args[2]).toString());
                } catch (Exception error) {
                    sender.tlMessage("command.accountrank.unknown-rank");
                    return;
                }

                if (rank == Rank.DEFAULT) {
                    sender.tlMessage("command.accountrank.cant-add-default-rank");
                    return;
                }

                if (sender.getHandle() instanceof ProxiedPlayer) {
                    if (Profile.getProfile(sender.getHandle()).getRank().ordinal() > rank.ordinal()) {
                        sender.tlMessage("command.accountrank.cant-add-that-rank");
                        return;
                    }
                }
                if (profile == null) {
                    UUID id;
                    if ((id = Commons.getUuidFetcher().getUUIDOf(args[0])) == null)
                        id = Commons.getUuidFetcher().getFixedOfflineUUID(args[0]);
                    try {
                        if ((profile = Commons.getStorageCommon().getAccountStorage().createProfile(id, args[0])) == null) {
                            String timeFormat = null;
                            if (args.length >= 4 && args[3].equalsIgnoreCase("-paid"))
                                timeFormat = "-1L";
                            else if (args.length >= 5 && args[4].equalsIgnoreCase("-paid"))
                                timeFormat = args[3];
                            if (timeFormat != null) {
                                List<String> list = null;
                                if (ProxyPlugin.getInstance().getConfig().get("paids-ranks-failed") == null)
                                    list = new ArrayList<>();
                                else
                                    list = ProxyPlugin.getInstance().getConfig().getStringList("paids-ranks-failed");
                                list.add("{\"name\":\"" + args[0] + "\",\"rank\":\"" + rank + "\",\"time\":\""
                                        + timeFormat + "\"}");
                                ProxyPlugin.getInstance().getConfig().set("paids-ranks-failed", list);
                                ProxyPlugin.getInstance().saveConfig();
                                sender.tlMessage("command.accountrank.could-not-create-profie-paid");
                                return;
                            } else {
                                sender.tlMessage("command.accountrank.could-not-create-profie");
                                return;
                            }
                        }
                    } catch (Exception error) {
                        error.printStackTrace();
                        sender.tlMessage(ChatColor.RED + error.toString());
                        return;
                    }
                }

                long time = -1;
                boolean isPaid = false;

                if (args.length >= 4 && args[3].equalsIgnoreCase("-paid"))
                    isPaid = true;

                if (!isPaid) {

                    if (args.length >= 4) {
                        try {
                            time = DateUtils.parseDateDiff(args[3]);
                        } catch (Exception error) {
                            sender.tlMessage("command.accountrank.illegal-time-format");
                            return;
                        }
                    }

                    if (args.length >= 5 && args[4].equalsIgnoreCase("-paid"))
                        isPaid = true;
                }

                ProxiedPlayer player = BungeeCord.getInstance().getPlayer(profile.getUniqueId());
                CommandSender bungeeSender = (CommandSender) sender.getHandle();
                RankProduct rankProduct = new RankProduct(rank, UUID.randomUUID().toString(),
                        bungeeSender instanceof ProxiedPlayer ? ((ProxiedPlayer) bungeeSender).getUniqueId()
                                : Commons.CONSOLE_UNIQUEID, bungeeSender.getName(), isPaid, time);

                profile.addRank(rankProduct);
                profile.save();

                if (player != null) {
                    Commons.getRedisBackend().saveRedisProfile(profile);
                    ProxyPlugin.getInstance().getPermissionManager().updatePermissions(player);
                }

                for (ProxiedPlayer pP : BungeeCord.getInstance().getPlayers()) {
                    if (!pP.hasPermission("core.cmd.accountrank"))
                        continue;
                    Profile gP = Profile.getProfile(pP);
                    pP.sendMessage(TextComponent.fromLegacyText(
                            Translator.tl(gP.getLocale(), "command.accountrank.added-rank", profile.getName(),
                                    profile.getUniqueId(), Tag.fromRank(rank).getFormattedName(),
                                    (time == -1 ? Translator.tl(gP.getLocale(), "core.translation.eternal")
                                            : DateUtils.getTime(time)),
                                    bungeeSender.getName(),
                                    (isPaid ? Translator.tl(gP.getLocale(), "core.translation.paid")
                                            : Translator.tl(gP.getLocale(), "core.translation.not-paid")))));
                }

                PacketOutAddRank pakage = new PacketOutAddRank(profile.getUniqueId(), rankProduct);
                Commons.getRedisBackend().publish(PacketType.ADD_RANK.toString(), Commons.getGson().toJson(pakage));
            } else if (args[1].equalsIgnoreCase("remove")) {
                if (profile != null) {
                    if (sender.getHandle() instanceof ProxiedPlayer) {
                        if (Profile.getProfile(sender.getHandle()).getRank().ordinal() > profile.getRank().ordinal()) {
                            sender.tlMessage("command.accountrank.cant-remove-rank-from-higher");
                            return;
                        }
                    }

                    if (ForceOP.isOP(profile.getUniqueId())) {
                        if (!(sender.getHandle() instanceof ProxiedPlayer) ||
                                !ForceOP.isOP(((ProxiedPlayer) sender.getHandle()).getUniqueId())) {
                            sender.sendMessage("§cVocê não pode remover ranks deste usuário!");
                            return;
                        }
                    }

                    int idx;
                    RankProduct rank;

                    try {
                        idx = Integer.valueOf(args[2]).intValue();
                    } catch (Exception error) {
                        sender.sendMessage(ChatColor.RED + error.toString());
                        return;
                    }

                    ProxiedPlayer player;
                    if ((rank = profile.removeRank(idx)) != null) {
                        profile.save();

                        if ((player = BungeeCord.getInstance().getPlayer(profile.getUniqueId())) != null) {
                            Commons.getRedisBackend().saveRedisProfile(profile);
                            ProxyPlugin.getInstance().getPermissionManager().updatePermissions(player);
                        }

                        for (ProxiedPlayer pP : BungeeCord.getInstance().getPlayers()) {
                            if (!pP.hasPermission("core.cmd.accountrank"))
                                continue;
                            Profile gP = Profile.getProfile(pP);
                            pP.sendMessage(TextComponent.fromLegacyText(
                                    Translator.tl(gP.getLocale(), "command.accountrank.removed-rank", profile.getName(),
                                            profile.getUniqueId(), Tag.fromRank(rank.getObject()).getFormattedName(),
                                            ((CommandSender) sender.getHandle()).getName())));
                        }

                        PacketOutRemoveRank pakage = new PacketOutRemoveRank(profile.getUniqueId(), idx);
                        Commons.getRedisBackend().publish(PacketType.REMOVE_RANK.toString(), Commons.getGson().toJson(pakage));
                    } else {
                        sender.tlMessage("command.accountrank.no-rank-for-id");
                    }
                } else {
                    sender.tlMessage("command.accountrank.profile-not-found");
                }
            } else {
                sender.tlMessage("command.accountrank.usage");
            }
        }
    }
}
