package com.lostmc.pvp.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.npc.NonPlayerController;
import org.bukkit.entity.Player;

public class NpcCommand extends WrappedBukkitCommand {

    public NpcCommand() {
        super("npc");
        this.setPermission("pvp.cmd.npc");
        this.setAliases("nonplayer");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile profile = Profile.getProfile(p);
            if (args.length == 0) {
                sender.tlMessage("pvp.command.npc.usage");
            } else {
                NonPlayerController.NonPlayerType npc;
                try {
                    npc = NonPlayerController.NonPlayerType.valueOf((args[0].replace("1v1", "FIGHT")).toUpperCase());
                } catch (Exception e) {
                    sender.tlMessage("pvp.command.npc.type-not-found", args[0]);
                    return;
                }
                PvP.getControl().getController(NonPlayerController.class)
                        .saveLocation(p.getLocation(), npc);
                sender.tlMessage("pvp.command.npc.success", npc.toString().toLowerCase());
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
