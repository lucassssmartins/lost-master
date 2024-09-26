package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand extends WrappedBukkitCommand implements Listener {

    static Map<UUID, UUID> lastTellUUID = new HashMap<>();

    public MessageCommand() {
        super("tell");
        setAliases("message", "msg", "whisper", "w");
        Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.getInstance());
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile profile = Profile.getProfile(p);
            if (args.length == 0) {
                p.sendMessage("§cUso: /tell <target> <message>");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (!profile.getData(DataType.PRIVATE_MESSAGES).getAsBoolean()) {
                        profile.setData(DataType.PRIVATE_MESSAGES, true);
                        profile.save();
                        p.sendMessage("§aVocê ativou as mensagens privadas.");
                    } else {
                        p.sendMessage("§cAs mensagens privadas já estão ativadas.");
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (profile.getData(DataType.PRIVATE_MESSAGES).getAsBoolean()) {
                        profile.setData(DataType.PRIVATE_MESSAGES, false);
                        profile.save();
                        p.sendMessage("§cVocê desativou as mensagens privadas.");
                    } else {
                        p.sendMessage("§cAs mensagens privadas já estão desativadas.");
                    }
                } else {
                    p.sendMessage("§cUso: /tell <target> <message>");
                }
            } else {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    if (!t.equals(p)) {
                        Profile tP = Profile.getProfile(t);
                        if (tP.getData(DataType.PRIVATE_MESSAGES).getAsBoolean()) {
                            String message = createArgs(1, args);
                            lastTellUUID.put(t.getUniqueId(), p.getUniqueId());
                            lastTellUUID.put(p.getUniqueId(), t.getUniqueId());
                            t.sendMessage("§7[Mensagem de §f" + p.getName() + "§7] " + message);
                            p.sendMessage("§7[Mensagem para §f" + t.getName() + "§7] " + message);
                        } else {
                            p.sendMessage(
                                    "§cEste jogador está com as mensagens privadas desativadas.");
                        }
                    } else {
                        p.sendMessage("§cUso: /tell <target> <message>");
                    }
                    t = null;
                } else {
                    p.sendMessage("§cJogador não encontrado.");
                }
            }
        }
    }

    static String createArgs(int begin, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < args.length; i++)
            sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
        return sb.toString();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastTellUUID.remove(event.getPlayer().getUniqueId());
    }
}
