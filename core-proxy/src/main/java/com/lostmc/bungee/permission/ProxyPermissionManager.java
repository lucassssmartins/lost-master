package com.lostmc.bungee.permission;

import java.util.List;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.core.permission.PermissionCheckResult;
import com.lostmc.core.permission.PermissionManager;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import com.google.common.collect.Lists;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyPermissionManager extends PermissionManager<ProxiedPlayer> implements Listener {

    private final ProxyPlugin plugin;

    public ProxyPermissionManager(ProxyPlugin plugin) {
        super();
        this.plugin = plugin;
        BungeeCord.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PostLoginEvent event) {
        updatePermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPermissionCheck(PermissionCheckEvent event) {
        CommandSender sender = event.getSender();
        if (sender instanceof ProxiedPlayer) {
            String check = event.getPermission();
            for (String perm : sender.getPermissions()) {
                if (check(perm, check) != PermissionCheckResult.TRUE)
                    continue;
                event.setHasPermission(true);
                return;
            }
            event.setHasPermission(false);
        }
    }

    private PermissionCheckResult check(String expression, String permission) {
        if (getMatcher().isMatches(expression, permission))
            return PermissionCheckResult.TRUE;
        return PermissionCheckResult.UNDEFINED;
    }

    @Override
    public void updatePermissions(ProxiedPlayer player) {
        for (String oldPerm : Lists.newArrayList(player.getPermissions()))
            player.setPermission(oldPerm, false);

        Profile profile = Profile.getProfile(player);

        for (RankProduct rank : profile.getRanks()) {
            for (String perm : this.plugin.getConfig()
                    .getStringList("permissions." + rank.getObject().toString().toLowerCase())) {
                if (player.hasPermission(perm.toLowerCase()))
                    continue;
                player.setPermission(perm.toLowerCase(), true);
            }
        }

        for (PermissionProduct permission : profile.getPermissions()) {
            if (player.hasPermission(permission.getObject().toLowerCase()))
                continue;
            player.setPermission(permission.getObject().toLowerCase(), true);
        }

        for (String perm : this.plugin.getConfig()
                .getStringList("permissions." + Rank.DEFAULT.toString().toLowerCase())) {
            if (player.hasPermission(perm.toLowerCase()))
                continue;
            player.setPermission(perm.toLowerCase(), true);
        }
    }

    @Override
    public void clearPermissions(ProxiedPlayer player) {

    }
}
