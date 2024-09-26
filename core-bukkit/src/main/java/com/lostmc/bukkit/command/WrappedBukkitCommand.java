package com.lostmc.bukkit.command;

import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public abstract class WrappedBukkitCommand extends WrappedCommand {

    public WrappedBukkitCommand(String name) {
        super(Command.class);
        setHandle(new BukkitCommand(name));
    }

    public void register(CommandMap commandMap) {
        BukkitCommand command = (BukkitCommand) getHandle();
        commandMap.register(getName().toLowerCase(), command);
    }

    class BukkitCommand extends Command {

        public BukkitCommand(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String label, String[] args) {
            Locale locale = commandSender instanceof Player ? Profile.getProfile(commandSender).
                    getLocale() : Commons.DEFAULT_LOCALE;

            if (!testPermissionSilent(commandSender)) {
                commandSender.sendMessage(Translator.tl(locale, "command-no-access"));
                return true;
            }

            WrappedCommandSender senderWrapper = new WrappedCommandSender(commandSender.getClass());
            senderWrapper.setHandle(commandSender);

            if (runAsync()) {
                Commons.getPlatform().runAsync(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            WrappedBukkitCommand.this.execute(senderWrapper, label, args);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else {
                try {
                    WrappedBukkitCommand.this.execute(senderWrapper, label, args);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return true;
        }

        /**
         * Player command tab completer; default is org.bukkit.command.Command.tabComplete()
         */
        public List<String> complete(WrappedCommandSender sender, String label, String[] args) {
            return null;
        }
    }
}
