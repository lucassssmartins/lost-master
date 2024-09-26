package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends WrappedBukkitCommand {

    public SetSpawnCommand() {
        super("setspawn");
        setPermission("hg.cmd.setspawn");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            if (((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
                Player p = (Player) sender.getHandle();
                HungerGames.spawnLocation = p.getLocation();
                p.sendMessage("§aSpawn point definido com sucesso!");
            } else {
                sender.sendMessage("§cO spawn point pode ser definido apenas no pré-game.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
