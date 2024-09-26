package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class MaintenanceCommand extends WrappedProxyCommand {

    public MaintenanceCommand() {
        super("whitelist");
        setPermission("core.cmd.maintenance");
        setAliases("maintenance");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /whitelist <add/remove> <player> / <on/off>");
        } else {
            ProxyPlugin plugin = ProxyPlugin.getInstance();
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (!plugin.isMaintenance()) {
                        plugin.setMaintenance(true);
                        sender.sendMessage("§aModo manutenção ativado!");
                        announce(sender, true);
                    } else {
                        sender.sendMessage("§cModo manutenção já ativado.");
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (plugin.isMaintenance()) {
                        plugin.setMaintenance(false);
                        sender.sendMessage("§cModo manutenção desativado!");
                        announce(sender, false);
                    } else {
                        sender.sendMessage("§cModo manutenção já desativado.");
                    }
                } else {
                    sender.sendMessage("§cUso: /whitelist <add/remove> <player> / <on/off>");
                }
            } else {
                if (args[0].equalsIgnoreCase("add")) {
                    String userName = args[1];
                    UUID uuid = Commons.getUuidFetcher().getUUIDOf(userName);
                    if (uuid == null)
                        uuid = Commons.getUuidFetcher().getFixedOfflineUUID(userName);
                    if (!plugin.isWhitelisted(uuid)) {
                        plugin.setWhitelisted(uuid, true);
                        whitelist(userName, uuid, true);
                    } else {
                        sender.sendMessage("§cEste jogador já está na whitelist.");
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    String userName = args[1];
                    UUID uuid = Commons.getUuidFetcher().getUUIDOf(userName);
                    if (uuid == null)
                        uuid = Commons.getUuidFetcher().getFixedOfflineUUID(userName);
                    if (plugin.isWhitelisted(uuid)) {
                        plugin.setWhitelisted(uuid, false);
                        whitelist(userName, uuid, false);
                    } else {
                        sender.sendMessage("§cEste jogador não está na whitelist.");
                    }
                } else {
                    sender.sendMessage("§cUso: /whitelist <add/remove> <player> / <on/off>");
                }
            }
        }
    }

    void announce(WrappedCommandSender sender, boolean b) {
        if (b) {
            for (ProxiedPlayer ps : ProxyServer.getInstance().getPlayers()) {
                if (ps.hasPermission(getPermission())) {
                    ps.sendMessage(new TextComponent(ChatColor.YELLOW + sender.getName() + " ativou o modo manutenção!"));
                }
            }
        } else {
            for (ProxiedPlayer ps : ProxyServer.getInstance().getPlayers()) {
                if (ps.hasPermission(getPermission())) {
                    ps.sendMessage(new TextComponent(ChatColor.YELLOW + sender.getName() + " desativou o modo manutenção!"));
                }
            }
        }
    }

    void whitelist(String userName, UUID uuid, boolean b) {
        if (b) {
            for (ProxiedPlayer ps : ProxyServer.getInstance().getPlayers()) {
                if (ps.hasPermission(getPermission())) {
                    ps.sendMessage(new TextComponent(ChatColor.YELLOW + userName
                            + "(" + uuid + ") foi adicionado na whitelist"));
                }
            }
        } else {
            for (ProxiedPlayer ps : ProxyServer.getInstance().getPlayers()) {
                if (ps.hasPermission(getPermission())) {
                    ps.sendMessage(new TextComponent(ChatColor.YELLOW + userName
                            + "(" + uuid + ") foi removido da whitelist"));
                }
            }
        }
    }
}
