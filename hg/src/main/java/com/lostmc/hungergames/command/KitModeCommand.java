package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.listener.InventoryListener;
import com.lostmc.hungergames.sidebar.HungerSidebarModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KitModeCommand extends WrappedBukkitCommand {

    public KitModeCommand() {
        super("kitmode");
        setPermission("hg.cmd.kitmode");
        setAliases("km");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        HungerGames hg = (HungerGames) HungerGames.getInstance();
        HungerGamesMode mode = (HungerGamesMode) hg.getGameMode();
        if (hg.getGameStage().isPregame()) {
            if (args.length == 0) {
                sender.sendMessage("§cUso: /kitmode <single, double>");
            } else if (args[0].equalsIgnoreCase("single")) {
                if (mode.isDoubleKit()) {
                    mode.setDoubleKit(false);
                    if (HungerSidebarModel.getSidebarTitle().contains("DOUBLEKIT")) {
                        String serverId = Commons.getProxyHandler().getLocal().getId();
                        HungerSidebarModel.setSidebarTitle("§6§lSINGLEKIT #" + serverId.replaceAll("[a-zA-Z0-]",
                                ""));
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        HungerGamer gamer = HungerGamer.getGamer(p);
                        if (gamer.isAlive()) {
                            gamer.getKits().remove(2);
                            p.getInventory().remove(InventoryListener.doubleChest);
                            p.updateInventory();
                        }
                    }
                    sender.sendMessage("§aVocê alterou o modo de kit para single.");
                    MessageAPI.sendAlert(sender.getName() + " alterou o modo de kit para single");
                } else {
                    sender.sendMessage("§cEste torneio já é single kit.");
                }
            } else if (args[0].equalsIgnoreCase("double")) {
                if (!mode.isDoubleKit()) {
                    mode.setDoubleKit(true);
                    if (HungerSidebarModel.getSidebarTitle().contains("SINGLEKIT")) {
                        String serverId = Commons.getProxyHandler().getLocal().getId();
                        HungerSidebarModel.setSidebarTitle("§6§lDOUBLEKIT #" + serverId.replaceAll("[a-zA-Z0-]",
                                ""));
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (HungerGamer.getGamer(p).isAlive()) {
                            p.getInventory().addItem(InventoryListener.doubleChest);
                            p.updateInventory();
                        }
                    }
                    sender.sendMessage("§aVocê alterou o modo de kit para double.");
                    MessageAPI.sendAlert(sender.getName() + " alterou o modo de kit para double");
                } else {
                    sender.sendMessage("§cEste torneio já é double kit.");
                }
            }
        } else {
            sender.sendMessage("§cO torneio já iniciou, portanto você não pode mais alterar esta opção!");
        }
    }
}
