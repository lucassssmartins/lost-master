package com.lostmc.game.command;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.entity.Player;

public class ScoreCommand extends WrappedBukkitCommand {

    public ScoreCommand() {
        super("score");
        this.setAliases("sidebar");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard((Player) sender.getHandle());
            if (scoreboard.isRegistered()) {
                scoreboard.unregister();
                sender.tlMessage("command.score.disabled");
            } else {
                scoreboard.register();
                sender.tlMessage("command.score.enabled");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
