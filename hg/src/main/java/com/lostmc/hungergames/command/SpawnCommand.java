package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.stage.GameStage;
import org.bukkit.entity.Player;

public class SpawnCommand extends WrappedBukkitCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            if (((HungerGames) HungerGames.getInstance()).getGameStage() == GameStage.WAITING ||
                    ((HungerGames) HungerGames.getInstance()).getGameStage() == GameStage.PREGAME) {
                HungerGames.teleportToSpawn((Player) sender.getHandle());
            } else {
                sender.sendMessage("§cComando desabilitado após o início do torneio!");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
