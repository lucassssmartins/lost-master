package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.manager.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ForceKitCommand extends WrappedBukkitCommand {

    public ForceKitCommand() {
        super("forcekit");
        setPermission("hg.cmd.forcekit");
        setAliases("forcekit1", "fkit", "fkit1");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUso: /forcekit <kit> <target/all>");
        } else {
            Kit kit = Management.getManagement(KitManager.class).getKitByName(args[0]);
            if (kit != null) {
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        HungerGamer gamer = HungerGamer.getGamer(ps);
                        if (gamer.isAlive()) {
                            gamer.setPrimaryKit(kit);
                            if (!((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
                                if (kit.getItems() != null) {
                                    ps.getInventory().addItem(kit.getItems());
                                }
                            }
                        }
                    }
                    sender.sendMessage("§aVocê forçou o kit " + kit.getName() + " como primário para todos" +
                            " os jogadores.");
                    MessageAPI.sendAlert(sender.getName() + " forçou o kit " + kit.getName() + " como" +
                            " primário para todos os jogadores");
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        HungerGamer gamer = HungerGamer.getGamer(target);
                        if (gamer.isAlive()) {
                            gamer.setPrimaryKit(kit);
                            if (!((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
                                if (kit.getItems() != null) {
                                    target.getInventory().addItem(kit.getItems());
                                }
                            }
                            sender.sendMessage("§aVocê forçou o kit " + kit.getName() + " como primário para " + target.getName() + ".");
                            MessageAPI.sendAlert(sender.getName() + " forçou o kit " + kit.getName() + " como" +
                                    " primário para " + target.getName());
                        } else {
                            sender.sendMessage("§cEste jogador não está participando do torneio.");
                        }
                    } else {
                        sender.sendMessage("§cJogador não encontrado.");
                    }
                }
            } else {
                sender.sendMessage("§cKit não encontrado.");
            }
        }
    }
}
