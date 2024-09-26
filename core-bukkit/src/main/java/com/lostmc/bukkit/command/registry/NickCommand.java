package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.nick.NickAPI;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NickCommand extends WrappedBukkitCommand {

    public NickCommand() {
        super("nick");
        setPermission("core.cmd.nick");
        setAliases("fake");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            Profile profile = Profile.getProfile(p);
            if (args.length == 0) {
                p.sendMessage("§cUso: /nick <nickname/random/list/reset>");
            } else if (args[0].equalsIgnoreCase("list")) {
                Set<Profile> nickedProfiles = new HashSet<>(Commons.getProfileMap().values());
                nickedProfiles.removeIf(e -> e.getData(DataType.NICKNAME).getAsString().isEmpty());
                if (!nickedProfiles.isEmpty()) {
                    p.sendMessage("§e--- §6Mostrando todos os nicks§e ---");
                    for (Profile nicked : nickedProfiles)
                        p.sendMessage("§eNick: §6" + nicked.getData(DataType.NICKNAME).getAsString() +
                                " §e- Name: §6" + nicked.getName());
                } else {
                    p.sendMessage("§cNo nicked players found.");
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!profile.getData(DataType.NICKNAME).getAsString().isEmpty()) {
                    profile.setData(DataType.NICKNAME, "");
                    Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> {
                        BukkitPlugin.getControl().getController(NametagController.class)
                                .reset(p.getName());
                        NickAPI.changePlayerName(p, profile.getName());
                        BukkitPlugin.getControl().getController(NametagController.class)
                                .setNametag(p, Tag.fromRank(profile.getRank()));
                        p.sendMessage("§aNick removido com sucesso!");
                    });
                } else {
                    p.sendMessage("§cVocê não está com um nick.");
                }
            } else if (args[0].equalsIgnoreCase("random")) {
                try {
                    String nick = generateNick();
                    Bukkit.getScheduler().runTaskLater(BukkitPlugin.getInstance(), () -> {
                        p.performCommand("nick " + nick);
                    }, 1);
                } catch (Exception e) {
                    p.sendMessage("§c" + e);
                }
            } else {
                String nick = args[0];
                if (nick.matches("[a-zA-Z0-9_]{3,16}")) {
                    if (Bukkit.getPlayer(nick) == null) {
                        if (Commons.searchProfile(nick) == null) {
                            if (Commons.getUuidFetcher().getUUIDOf(nick) == null) {
                                profile.setData(DataType.NICKNAME, nick);
                                Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> {
                                    BukkitPlugin.getControl().getController(NametagController.class)
                                            .reset(p.getName());
                                    NickAPI.changePlayerName(p, nick);
                                    BukkitPlugin.getControl().getController(NametagController.class)
                                            .setNametag(p, Tag.DEFAULT);
                                    p.sendMessage("§aNickname alterado com sucesso!");
                                });
                            } else {
                                p.sendMessage("§cVocê não pode utilizar nicknames registrados em minecraft.net.");
                            }
                        } else {
                            p.sendMessage("§cEste nick não pode ser usado.");
                        }
                    } else {
                        p.sendMessage("§cUm jogador já está usando este nick.");
                    }
                } else {
                    p.sendMessage("§cO nick precisa respeitar a padronização '[a-zA-Z0-9_]{3,16}'");
                }
            }
        }
    }

    private String generateNick() {
        int i = 0;
        while (i < 50) {
            StringBuilder s = new StringBuilder();
            String alphabetic = "abcdefghijklmnopqrstuvxwyz0123456789_";
            Random r = Commons.getRandom();
            int length = r.nextInt(10) >= 6 ? (r.nextInt(10) <= 5 ? 10 : 15) : 5;
            while (s.length() < length) {
                char next = alphabetic.charAt(r.nextInt(alphabetic.length()));
                if (r.nextInt(5) <= 3)
                    s.append(Character.toUpperCase(next));
                else
                    s.append(Character.toLowerCase(next));
            }
            if (Commons.getUuidFetcher().getUUIDOf(s.toString()) != null
                    || Commons.searchProfile(s.toString()) != null
                    || Bukkit.getPlayer(s.toString()) != null) {
                ++i;
                continue;
            }
            return s.toString();
        }
        throw new IllegalStateException("The nick generator exceeded more than 50 attempts.");
    }
}
