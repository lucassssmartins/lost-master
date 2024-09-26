package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.menu.TeamMenu;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.property.IProperty;
import com.lostmc.core.property.IPropertyGetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TeamCommand extends WrappedBukkitCommand implements Listener {

    private Set<UUID> opening = new HashSet<>();

    public TeamCommand() {
        super("team");
        setPermission("core.cmd.team");
        setAliases("equipe");
        Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.getInstance());
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();

            if (opening.contains(p.getUniqueId())) {
                p.sendMessage("§cO menu de membros da equipe ja está carregando!");
                return;
            }

            opening.add(p.getUniqueId());
            p.sendMessage("§eCarregando lista de membros da equipe...");
            List<Map.Entry<Profile, IProperty>> teamList;

            try {
                teamList = getTeam();
            } catch (Exception e) {
                p.sendMessage("§cFalha ao carregar: " + e);
                return;
            }

            p.sendMessage("§eCarregado!");
            p.openInventory(new TeamMenu(teamList));

            opening.remove(p.getUniqueId());
        } else {
            sender.sendInGameMessage();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        opening.remove(e.getPlayer().getUniqueId());
    }

    public List<Map.Entry<Profile, IProperty>> getTeam() throws SQLException {
        List<Map.Entry<Profile, IProperty>> teamList = new ArrayList<>();
        List<Profile> founded = new ArrayList<>();

        try (Connection conn = Commons.getMysqlBackend().getDataSource().getConnection()) {
            String tableName = Commons.getStorageCommon().getAccountStorage().getTableName();
            try (PreparedStatement stmt = conn
                    .prepareStatement("SELECT * FROM `" + tableName +
                            "` WHERE " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.ADMIN + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.MANAGER + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.MODPLUS + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.MOD + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.TRIAL + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.BUILDER + "%'"
                            + " OR " +
                            "JSON_EXTRACT(`json`, '$.ranks[*].object') LIKE '%" + Rank.YTPLUS + "%'")) {
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        founded.add(Commons.getGson().fromJson(result.getString(2), Profile.class));
                    }
                }
            }
        }

        founded.sort(Comparator.comparing(this::getSort));
        for (Profile found : founded) {
            teamList.add(new AbstractMap.SimpleEntry<>(found,
                    IPropertyGetter.getProperty(found.getUniqueId().toString())));
        }

        return teamList;
    }

    private String getSort(Profile profile) {
        String aphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return String.valueOf(aphabet.charAt(profile.getRank().ordinal()));
    }
}
