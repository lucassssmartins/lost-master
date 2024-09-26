package com.lostmc.bungee.command;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.translate.Translator;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.util.List;
import java.util.Locale;

public abstract class WrappedProxyCommand extends WrappedCommand {

    public WrappedProxyCommand(String name) {
        super(Command.class);
        this.setHandle(new BungeeCommand(name));
        setPermissionMessage("§cVocê não possui permissão para executar este comando.");
    }

    public void register(PluginManager manager) {
        manager.registerCommand(ProxyPlugin.getInstance(), (Command) getHandle());
    }

    class BungeeCommand extends Command implements TabExecutor {

        public BungeeCommand(String name) {
            super(name);
        }

        @Override
        public void execute(CommandSender commandSender, String[] args) {
            WrappedCommandSender senderWrapper = new WrappedCommandSender(commandSender.getClass());
            senderWrapper.setHandle(commandSender);

            if (!testPermission(commandSender, senderWrapper.getLocale())) {
                return;
            }

            if (runAsync()) {
                Commons.getPlatform().runAsync(() -> {
                    try {
                        WrappedProxyCommand.this.execute(senderWrapper, getName(),
                                args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                try {
                    WrappedProxyCommand.this.execute(senderWrapper, getName(),
                            args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean testPermission(CommandSender sender, Locale locale) {
            if (getPermission() != null && !getPermission().isEmpty()) {
                if (!sender.hasPermission(getPermission())) {
                    sender.sendMessage(TextComponent.fromLegacyText(getPermissionMessage()));
                    return false;
                }
            }
            return true;
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            WrappedCommandSender senderWrapper = new WrappedCommandSender(sender.getClass());
            senderWrapper.setHandle(sender);

            Iterable<String> completions = complete(senderWrapper, args);
            if (completions != null) {
                return completions;
            }

            final String lastArg = (args.length > 0) ? args[args.length - 1].toLowerCase(Locale.ROOT) : "";
            return Iterables.transform(Iterables.filter(ProxyServer.getInstance().getPlayers(),
                    player -> player.getName().toLowerCase(Locale.ROOT).startsWith(lastArg)),
                    player -> player.getName());
        }
    }

    public List<String> complete(WrappedCommandSender sender, String[] args) {
        return null;
    }
}
