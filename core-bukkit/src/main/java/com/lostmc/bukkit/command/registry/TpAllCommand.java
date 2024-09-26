package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.message.MessageAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.event.tpall.PlayerTpAllEvent;
import com.lostmc.core.command.WrappedCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TpAllCommand extends WrappedBukkitCommand {

    private boolean teleporting = false;

    public TpAllCommand() {
        super("tpall");
        setPermission("core.cmd.tpall");
        setAliases("synctpall");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Location tp = p.getLocation();
            if (!teleporting) {
                Queue<Player> tpQueue = new ConcurrentLinkedQueue<>(Bukkit.getOnlinePlayers());
                tpQueue.removeIf(o -> o.getUniqueId().equals(p.getUniqueId()));
                if (!tpQueue.isEmpty()) {
                    teleporting = true;
                    MessageAPI.sendAlert(p.getName() + " iniciou um teleporte global");
                    Bukkit.broadcastMessage("§aIniciando teleporte global...");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (!tpQueue.isEmpty()) {
                                Player next = tpQueue.poll();
                                PlayerTpAllEvent event = new PlayerTpAllEvent(next);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled())
                                    next.teleport(tp);
                            } else {
                                cancel();
                                teleporting = false;
                                Bukkit.broadcastMessage("§aTeleporte global concluído com sucesso!");
                            }
                        }
                    }.runTaskTimer(BukkitPlugin.getInstance(), 2, 2);
                } else {
                    p.sendMessage("§cNenhum jogador para teleportar.");
                }
            } else {
                p.sendMessage("§cAguarde para usar este comando.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
