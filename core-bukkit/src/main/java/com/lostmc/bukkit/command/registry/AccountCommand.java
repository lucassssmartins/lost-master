package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.menu.AccountMenu;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.rank.Rank;
import org.bukkit.entity.Player;

public class AccountCommand extends WrappedBukkitCommand {

    public AccountCommand() {
        super("account");
        this.setAliases("acc", "statistics", "stats");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile opener = Profile.getProfile(p);
            if (args.length == 0) {
                ((Player) sender.getHandle()).openInventory(new AccountMenu(opener, opener, false, false));
            } else {
                Profile target = Commons.searchProfile(args[0]);
                if (target != null) {
                    p.openInventory(new AccountMenu(target, opener, true, opener.getRank().ordinal() <= Rank.TRIAL.ordinal()));
                } else {
                    sender.tlMessage("command.account.not-found");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
