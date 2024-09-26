package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;

public class StartCommand extends WrappedBukkitCommand {

    public StartCommand() {
        super("start");
        setPermission("hg.cmd.start");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
            ((HungerGames) HungerGames.getInstance()).getGameMode().startGame();
            sender.sendMessage("§aVocê iniciou o torneio!");
        } else {
            sender.sendMessage("§cO torneio já iniciou.");
        }
    }
}
