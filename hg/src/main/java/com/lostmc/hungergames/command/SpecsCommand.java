package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpecsCommand extends WrappedBukkitCommand {

    public SpecsCommand() {
        super("specs");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            HungerGamer gamer = HungerGamer.getGamer(p);
            VanishController manager = HungerGames.getControl().getController(VanishController.class);
            if (((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
                p.sendMessage("§cComando desabilitado no estágio atual.");
            } else if (args.length == 0) {
                p.sendMessage("§cUso: /specs <on/off>");
            } else if (args[0].equalsIgnoreCase("on")) {
                gamer.setHiddingSpecs(false);
                manager.updateVanishToPlayer(p);
                for (Player ps : Bukkit.getOnlinePlayers()) {
                    if (!ps.equals(p)) {
                        if (HungerGamer.getGamer(ps).isNotPlaying()) {
                            if (HungerGames.getControl().getController(VanishController.class)
                                    .isVanished(ps)) {
                                if (Profile.getProfile(ps).getRank().ordinal() <
                                        Profile.getProfile(p).getRank().ordinal()) {
                                    p.hidePlayer(ps);
                                    continue;
                                }
                            }
                            p.showPlayer(ps);
                        } else if (!p.canSee(ps)) {
                            p.showPlayer(ps);
                        }
                    }
                }
                p.sendMessage("§aVocê agora vê os espectadores.");
            } else if (args[0].equalsIgnoreCase("off")) {
                gamer.setHiddingSpecs(true);
                for (Player ps : Bukkit.getOnlinePlayers()) {
                    if (!ps.equals(p)) {
                        HungerGamer hGamer = HungerGamer.getGamer(ps);
                        if (!hGamer.isAlive()) {
                            p.hidePlayer(ps);
                        }
                    }
                }
                p.sendMessage("§cVocê agora não vê os espectadores.");
            } else {
                p.sendMessage("§cUso: /specs <on/off>");
            }
        }
    }
}
