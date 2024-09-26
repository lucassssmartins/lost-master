package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class SkitCommand extends WrappedBukkitCommand {

    private Map<String, SimpleKit> simpleKits = new HashMap<>();

    public SkitCommand() {
        super("skit");
        setPermission("hg.cmd.skit");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (((HungerGames) HungerGames.getInstance()).getGameStage().isPregame()) {
            sender.sendMessage("§cComando indisponível no estágio atual.");
        } else if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length <= 1) {
                p.sendMessage("§cUso: /skit <apply/create/delete> <name>");
            } else {
                if (args[0].equalsIgnoreCase("apply") || args[0].equalsIgnoreCase("aplicar")) {
                    SimpleKit simpleKit = getSimpleKit(args[1]);
                    if (simpleKit != null) {
                        for (Player ps : Bukkit.getOnlinePlayers()) {
                            HungerGamer gamer = HungerGamer.getGamer(ps);
                            if (gamer.isNotPlaying())
                                continue;
                            PlayerInventory inventory = ps.getInventory();
                            inventory.setContents(simpleKit.getContents());
                            inventory.setArmorContents(simpleKit.getArmorContents());
                            ps.updateInventory();
                        }
                        sender.sendMessage("§aVocê aplicou o kit " + simpleKit.getName() + " á todos jogadores do torneio.");
                        MessageAPI.sendAlert(sender.getName() + " aplicou aos jogadores o kit " + simpleKit.getName());
                    } else {
                        sender.sendMessage("§cKit não encontrado.");
                    }
                } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("criar")) {
                    SimpleKit simpleKit = getSimpleKit(args[1]);
                    if (simpleKit == null) {
                        simpleKit = new SimpleKit(args[1], p.getInventory().getContents(),
                                p.getInventory().getArmorContents());
                        saveSimpleKit(simpleKit);
                        sender.sendMessage("§aVocê criou o kit '" + simpleKit.getName() + "'");
                        MessageAPI.sendAlert(sender.getName() + " criou o kit " + simpleKit.getName());
                    } else {
                       sender.sendMessage("§cO kit '" + simpleKit.getName() + "' já existe.");
                    }
                } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("deletar")) {
                    SimpleKit simpleKit = deleteSimpleKit(args[1]);
                    if (simpleKit != null) {
                        sender.sendMessage("§aVocê deletou o kit '" + simpleKit.getName() + "'!");
                        MessageAPI.sendAlert(sender.getName() + " criou o kit " + simpleKit.getName());
                    } else {
                        sender.sendMessage("§cO kit '" + args[1] + "' não foi criado.");
                    }
                } else {
                    p.sendMessage("§cUso: /skit <apply/create/delete> <name>");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public SimpleKit getSimpleKit(String name) {
        return simpleKits.get(name.toLowerCase());
    }

    public void saveSimpleKit(SimpleKit simpleKit) {
        simpleKits.put(simpleKit.getName().toLowerCase(), simpleKit);
    }

    public SimpleKit deleteSimpleKit(String name) {
        return simpleKits.remove(name.toLowerCase());
    }

    @Getter
    @AllArgsConstructor
    class SimpleKit {

        private String name;
        private ItemStack[] contents;
        private ItemStack[] armorContents;
    }
}
