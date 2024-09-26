package com.lostmc.hungergames.command;

import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.utils.DateUtils;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.manager.FeastManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.structure.Feast;
import com.lostmc.hungergames.structure.items.FeastType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class FeastManagerCommand extends WrappedBukkitCommand {

    public FeastManagerCommand() {
        super("feastmanager");
        setPermission("hg.cmd.feastmanager");
        setAliases("feastcontrol");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            GameStage gameStage = ((HungerGames) HungerGames.getInstance()).getGameStage();
            if (!gameStage.isInvincibility() && !gameStage.isGametime()) {
                p.sendMessage("§cComando desabilitado no estágio atual.");
            } else if (args.length == 0) {
                sendUsages(p);
            } else {
                FeastManager manager = Management.getManagement(FeastManager.class);
                if (args[0].equalsIgnoreCase("list")) {
                    List<Feast> pending = manager.getPendingFeasts();
                    if (!pending.isEmpty()) {
                        for (Feast feast : pending) {
                            p.sendMessage("§eFeast §6#" + feast.getId() + " §e- name='§6" + feast.getName()
                                    + "§e', type=§6" + feast.getChestStructure().getType()
                                    + "§e, location=[§6" + toString(feast.getCentralLocation()) + "§e], "
                                    + "timer=§6" + DateUtils.formatDifference(feast.getCounter()));
                        }
                    } else {
                        p.sendMessage("§cNenhum feast pendente encontrado.");
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("cancel")) {
                        try {
                            int id = Integer.parseInt(args[1]);
                            Feast target = manager.getFeast(id);
                            if (target != null) {
                                manager.cancelFeast(target.getId());
                                p.sendMessage("§aFeast #" + id + " cancelado com sucesso!");
                                MessageAPI.sendAlert(p.getName() + " cancelou o feast #" + id + " (" + target.getName()
                                        + ")");
                            } else {
                                p.sendMessage("§cFeast #" + id + " não encontrado.");
                            }
                        } catch (Exception e) {
                            p.sendMessage("§cO id precisa ser um número inteiro.");
                        }
                    } else if (args[0].equalsIgnoreCase("force")) {
                        try {
                            int id = Integer.parseInt(args[1]);
                            Feast target = manager.getFeast(id);
                            if (target != null) {
                                if (!target.isSpawned())
                                    target.spawn();
                                if (!target.isChestsSpawned())
                                    target.spawnChests();
                                Location loc = target.getCentralLocation();
                                if (target.isDefault())
                                    Bukkit.broadcastMessage("§cO feast principal spawnou em (" + loc.getBlockX()
                                            + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
                                 else
                                    Bukkit.broadcastMessage("§cO feast " + target.getName() + " #" + target.getId()
                                            + " spawnou em (" + loc.getBlockX() + ", " + loc.getBlockY() + ", "
                                            + loc.getBlockZ() + ")");
                                p.sendMessage("§aFeast #" + id + " cancelado com sucesso!");
                                MessageAPI.sendAlert(p.getName() + " forçou o feast #" + id + " (" + target.getName()
                                        + ")");
                            } else {
                                p.sendMessage("§cFeast #" + id + " não encontrado.");
                            }
                        } catch (Exception e) {
                            p.sendMessage("§cNúmero inválido para o id do feast.");
                        }
                    } else {
                        sendUsages(p);
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("settime")) {
                        int id;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            p.sendMessage("§cId inválido para o feast: " + e);
                            return;
                        }
                        Feast feast = manager.getFeast(id);
                        if (feast != null) {
                            long time;
                            try {
                                time = DateUtils.parseDateDiff(args[2], true);
                            } catch (Exception e) {
                                p.sendMessage("§cFormato de tempo inválido.");
                                return;
                            }
                            time = (time - System.currentTimeMillis()) / 1000;
                            if (!feast.isSpawned())
                                feast.spawn();
                            feast.setCounter((int) time);
                            p.sendMessage("§aVocê alterou o tempo do feast #" + feast.getId() + " ("
                                    + feast.getName() + ") para " + DateUtils.formatDifference(time));
                            MessageAPI.sendAlert(p.getName() + " alterou o tempo do feast #" + feast.getId() + " (" + feast.getName() + ") para " + DateUtils.formatDifference(time));
                        } else {
                            p.sendMessage("§cFeast #" + id + " não encontrado.");
                        }
                    }
                } else if (args.length == 7) {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (!manager.getPendingFeasts().isEmpty()) {
                            p.sendMessage("§cJá existe um feast pendente para spawnar! Cancele com /feastmanager <cancel> <id>");
                        } else {
                            FeastType type = null;
                            try {
                                type = FeastType.valueOf(args[1].toUpperCase());
                            } catch (Exception e) {
                            }
                            if (type != null) {
                                long time;
                                try {
                                    time = DateUtils.parseDateDiff(args[2], true);
                                } catch (Exception e) {
                                    p.sendMessage("§cFormato de tempo inválido.");
                                    return;
                                }
                                time = (time - System.currentTimeMillis()) / 1000;
                                int radius;
                                try {
                                    radius = Integer.parseInt(args[3]);
                                } catch (Exception e) {
                                    p.sendMessage("§cNúmero inválido para o raio do feast.");
                                    return;
                                }
                                if (radius <= 4 || radius > 40) {
                                    p.sendMessage("§cO raio precisa ser maior que 4, menor ou igual a 40.");
                                    return;
                                }
                                int x, y, z;
                                try {
                                    x = Integer.parseInt(args[4]);
                                    y = Integer.parseInt(args[5]);
                                    z = Integer.parseInt(args[6]);
                                } catch (Exception e) {
                                    p.sendMessage("§cCordenadas do feast inválidas: " + e);
                                    return;
                                }
                                Feast custom = new Feast("Custom Feast", radius, 150, type,
                                        new Location(p.getWorld(), x, y, z), (int) time);
                                manager.addFeast(custom);
                                p.sendMessage("§aVocê adicionou o feast " + custom.getName() + " #" + custom.getId() + "!");
                                TextComponent text = new TextComponent("§6§lCLIQUE AQUI");
                                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
                                text.addExtra("§e, para se teleportar ao feast.");
                                p.spigot().sendMessage(text);
                                MessageAPI.sendAlert(p.getName() + " adicionou o feast "
                                        + custom.getName() + " #" + custom.getId() + "(" +
                                        toString(custom.getCentralLocation()) + ")");
                            } else {
                                p.sendMessage("§cTipo de feast '" + args[1] + "' inexistente.");
                            }
                        }
                    } else {
                        sendUsages(p);
                    }
                } else {
                    sendUsages(p);
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public void sendUsages(Player p) {
        p.sendMessage("§cGerencidador de Feast:");
        p.sendMessage("§c/feastmanager <list>");
        p.sendMessage("§c/feastmanager <add> <type> <spawn-in time format> <radius> <x> <y> <z>");
        p.sendMessage("§c/feastmanager <force/cancel> <id>");
        p.sendMessage("§c/feastmanager <settime> <id> <time format>");
    }

    public String toString(Location location) {
        if (location == null)
            return "Not spawned";
        return "x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: "
                + location.getBlockZ();
    }
}
