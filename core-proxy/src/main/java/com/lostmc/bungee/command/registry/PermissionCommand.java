package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.networking.PacketOutAddPermission;
import com.lostmc.core.networking.PacketOutRemovePermission;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PermissionCommand extends WrappedProxyCommand {

    public PermissionCommand() {
        super("permission");
        setPermission("core.cmd.permission");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage("§cUso: /permission <target> <add/remove> <permission> [time]");
        } else {
            String userName = args[0];
            Profile profile = Commons.searchProfile(args[0]);
            if (profile == null && args[1].equalsIgnoreCase("add")) {
                UUID uuid = Commons.getUuidFetcher().getUUIDOf(userName);
                if (uuid == null)
                    uuid = Commons.getUuidFetcher().getFixedOfflineUUID(userName);
                try {
                    if ((profile = Commons.getStorageCommon().getAccountStorage().createProfile(uuid, userName)) == null) {
                        sender.sendMessage("§cFalha ao criar conta do usuário, tente novamente mais tarde.");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cImpossível criar a conta do usuario, " + e);
                    return;
                }
            } else if (profile == null && args[1].equalsIgnoreCase("remove")) {
                sender.sendMessage("§cUsuário não encontrado.");
                return;
            }
            if (args[1].equalsIgnoreCase("add")) {
                String tag = args[2];
                if (sender.getHandle() instanceof ProxiedPlayer) {
                    if (!((ProxiedPlayer) sender.getHandle()).hasPermission(tag)) {
                        sender.sendMessage("§cVocê não possui esta permissão, portanto, não pode adicioná-la");
                        return;
                    }
                }
                long time = -1;
                if (args.length >= 4) {
                    try {
                        time = DateUtils.parseDateDiff(args[3]);
                    } catch (Exception e) {
                        sender.sendMessage("§cFormato de tempo inválido: " + e);
                        return;
                    }
                }
                PermissionProduct permission = new PermissionProduct(tag, "permission",
                        (sender.getHandle() instanceof ProxiedPlayer ?
                                ((ProxiedPlayer) sender.getHandle()).getUniqueId() :
                                Commons.CONSOLE_UNIQUEID), sender.getName(), false,
                        time);
                profile.addPermission(permission);
                profile.save();
                sender.sendMessage("§ePermissão '" + tag + "' adicionada com sucesso!");
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (!player.hasPermission(getPermission()))
                        continue;
                    player.sendMessage(TextComponent.fromLegacyText("§e" + profile.getName() + "(" + profile.getUniqueId() + ") recebeu a permissão '"
                            + tag + "' com duração " + (time == -1 ? "eterna" : "de " + DateUtils.getTime(time)) + " via requisição de " + sender.getName() +"."));
                }
                ProxiedPlayer who = ProxyServer.getInstance().getPlayer(profile.getUniqueId());
                if (who != null) {
                    Commons.getRedisBackend().saveRedisProfile(profile);
                    ProxyPlugin.getInstance().getPermissionManager().updatePermissions(who);
                    who.sendMessage(TextComponent.fromLegacyText(
                            time == -1 ? "§aVocê recebeu a permissão '" + tag + "' com duração eterna."
                            : "§aVocê recebeu a permissão '" + tag + "' com duração de " + DateUtils.getTime(time)));
                    Commons.getRedisBackend().publish(PacketType.ADD_OUT_PERMISSION.toString(),
                            new PacketOutAddPermission(who.getUniqueId(), permission).toJson());
                }
            } else if (args[1].equalsIgnoreCase("remove")) {
                int removed = profile.removePermissions(args[2]);
                if (removed > 0) {
                    profile.save();
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if (!player.hasPermission(getPermission()))
                            continue;
                        player.sendMessage(TextComponent.fromLegacyText("§e" + profile.getName() + "(" + profile.getUniqueId() + ") " +
                                "teve a permissão '" + args[2] + "' removida da conta via requisição de " + sender.getName()));
                    }
                    ProxiedPlayer who = ProxyServer.getInstance().getPlayer(profile.getUniqueId());
                    if (who != null) {
                        Commons.getRedisBackend().saveRedisProfile(profile);
                        ProxyPlugin.getInstance().getPermissionManager().updatePermissions(who);
                        who.sendMessage(TextComponent.fromLegacyText(
                                "§cA permissão '" + args[2] + "' foi removida de sua conta."));
                        Commons.getRedisBackend().publish(PacketType.REMOVE_OUT_PERMISSIONS.toString(),
                                new PacketOutRemovePermission(who.getUniqueId(), args[2]).toJson());
                    }
                } else {
                    sender.sendMessage("§cEste jogador não possui esta permissão.");
                }
            } else {
                sender.sendMessage("§cUso: /permission <nick> <add/remove> <permission> [time]");
            }
        }
    }
}
