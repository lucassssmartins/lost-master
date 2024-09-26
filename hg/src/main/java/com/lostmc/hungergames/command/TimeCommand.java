package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.utils.DateUtils;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.stage.GameStage;

public class TimeCommand extends WrappedBukkitCommand {

    public TimeCommand() {
        super("time");
        setPermission("hg.cmd.time");
        setAliases("tempo");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /time <format>");
        } else {
            GameStage gameStage = ((HungerGames) HungerGames.getInstance()).getGameStage();
            if (gameStage != GameStage.WAITING) {
                if (!gameStage.isEnding()) {
                    long time;
                    try {
                        time = DateUtils.parseDateDiff(args[0], true);
                    } catch (Exception e) {
                        sender.sendMessage("§cFormato inválido: " + e);
                        return;
                    }
                    time = (time - System.currentTimeMillis()) / 1000;
                    ((HungerGames) HungerGames.getInstance()).setTimer((int) time);
                    sender.sendMessage("§aVocê alterou o tempo para " + DateUtils.formatDifference(time));
                    MessageAPI.sendAlert(sender.getName() + " alterou o tempo do torneio para " + DateUtils.formatDifference(time));
                } else {
                    sender.sendMessage("§cO torneio já está finalizando.");
                }
            } else {
                sender.sendMessage("§cO torneio está aguardando jogadores! Use /start para iniciá-lo.");
            }
        }
    }
}
