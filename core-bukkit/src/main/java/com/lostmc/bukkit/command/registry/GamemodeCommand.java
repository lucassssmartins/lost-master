package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand extends WrappedBukkitCommand {

    public GamemodeCommand() {
        super("gamemode");
        setPermission("core.cmd.gamemode");
        setAliases("gm");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUso: /gamemode <mode/id> [player]");
            } else {
                GameMode mode = null;
                try {
                    mode = GameMode.getByValue(Integer.parseInt(args[0]));
                } catch (Exception e) {
                    try {
                        mode = GameMode.valueOf(args[0].toUpperCase());
                    } catch (Exception e2) {
                    }
                }
                if (args.length == 1) {
                    if (mode != null) {
                        if (p.getGameMode() != mode) {
                            p.setGameMode(mode);
                            if (p.getGameMode() == mode) {
                                p.sendMessage("§aModo de jogo alterado para " + mode.toString().toLowerCase() + " com sucesso!");
                                MessageAPI.sendAlert(p.getName() + " alterou o próprio modo de jogo para " + mode.toString().toLowerCase());
                            }
                        } else {
                            p.sendMessage("§cVocê já está neste modo de jogo.");
                        }
                    } else {
                        p.sendMessage("§cModo de jogo não encontrado.");
                    }
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        if (!target.equals(p)) {
                            if (target.getGameMode() != mode) {
                                target.setGameMode(mode);
                                if (target.getGameMode() == mode) {
                                    p.sendMessage("§aModo de jogo de " + target.getName() + " alterado para " + mode.toString().toLowerCase()
                                            + " com sucesso!");
                                    MessageAPI.sendAlert(p.getName() + " alterou o modo de jogo de " + target.getName() + " para "
                                            + mode.toString().toLowerCase());
                                } else {
                                    p.sendMessage("§cNão foi possível alterar o modo de jogo de " + target.getName() + ".");
                                }
                            } else {
                                p.sendMessage("§c" + target.getName() + " já está no modo " + mode.toString().toLowerCase());
                            }
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitPlugin.getInstance(), () -> {
                                p.performCommand("gamemode " + args[0]);
                            });
                        }
                    } else {
                        p.sendMessage("§cJogador não encontrado.");
                    }
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
