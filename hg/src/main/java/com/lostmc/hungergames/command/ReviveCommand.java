package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.stage.GameStage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReviveCommand extends WrappedBukkitCommand {

    public ReviveCommand() {
        super("revive");
        setPermission("hg.cmd.revive");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        GameStage gameStage = ((HungerGames) HungerGames.getInstance()).getGameStage();
        if (!gameStage.isInvincibility() && !gameStage.isGametime()) {
            sender.sendMessage("§cComando indisponível no estágio atual.");
        } else if (args.length == 0) {
            sender.sendMessage("§cUso: /revive <target>");
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                HungerGamer gamer = HungerGamer.getGamer(target);
                if (!gamer.isAlive()) {
                    VanishController manager = HungerGames.getControl().getController(VanishController.class);
                    if (manager.isVanished(target))
                        manager.toggleVanish(target, PlayerVanishModeEvent.Mode.PLAYER);
                    if (!gamer.isAlive())
                        gamer.setGamerState(HungerGamer.GamerState.ALIVE);
                    sender.sendMessage("§a" + target.getName() + " agora está jogando o torneio!");
                    MessageAPI.sendAlert(sender.getName() + " reviveu " + target.getName());
                } else {
                    sender.sendMessage("§cEste jogador já está jogando o torneio.");
                }
            }
        }
    }
}
