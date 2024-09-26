package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.storage.account.AccountStorage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TruncateCommand extends WrappedProxyCommand {

    public TruncateCommand() {
        super("truncate_profile-holder");
        setPermission("*");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (!(sender.getHandle() instanceof ProxiedPlayer)) {
            try {
                try (Connection connection = Commons.getMysqlBackend().getDataSource().getConnection()) {
                    PreparedStatement stmt = connection.prepareStatement("TRUNCATE `profile_holder`");
                    stmt.execute();
                    sender.sendMessage("§aDone.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cError: " + e);
            }
        } else {
            sender.sendMessage("no_permission");
        }
    }
}
