package com.lostmc.bukkit.permission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.permission.injector.RegexPermissions;
import com.lostmc.core.permission.PermissionManager;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;

import lombok.Getter;

@Getter
public class BukkitPermissionManager extends PermissionManager<Player> {

	private BukkitPlugin plugin;
	private RegexPermissions regex;
	private final Map<UUID, PermissionAttachment> attachments;
	
	public BukkitPermissionManager(BukkitPlugin plugin) {
		super();
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(new PermissionListener(this), plugin);
		this.regex = new RegexPermissions(this);
		this.attachments = new HashMap<>();
	}

	@Override
	public void updatePermissions(Player player) {
		PermissionAttachment attach = (PermissionAttachment) attachments.get(player.getUniqueId());
		Permission playerPerm = getCreateWrapper(player, player.getUniqueId().toString());
		
		if (attach == null) {
			attach = player.addAttachment(getPlugin());
			attachments.put(player.getUniqueId(), attach);
			attach.setPermission(playerPerm, true);
		}
		
		playerPerm.getChildren().clear();

		Profile profile = Profile.getProfile(player);

		for (RankProduct rank : profile.getRanks()) {
			for (String perm : getPlugin().getConfig()
					.getStringList("permissions." + rank.getObject().toString().toLowerCase())) {
				if (!playerPerm.getChildren().containsKey(perm)) {
					playerPerm.getChildren().put(perm, true);
				}
			}
		}

		for (PermissionProduct perm : profile.getPermissions()) {
			if (!playerPerm.getChildren().containsKey(perm.getObject().toLowerCase())) {
				playerPerm.getChildren().put(perm.getObject().toLowerCase(), true);
			}
		}

		for (String perm : getPlugin().getConfig()
				.getStringList("permissions." + Rank.DEFAULT.toString().toLowerCase())) {
			if (!playerPerm.getChildren().containsKey(perm)) {
				playerPerm.getChildren().put(perm, true);
			}
		}

		player.recalculatePermissions();
	}

	private Permission getCreateWrapper(Player player, String name) {
		Permission perm = getServer().getPluginManager().getPermission(name);
		
		if (perm == null) {
			perm = new Permission(name, "Bukkit's default permission", PermissionDefault.FALSE);
			getServer().getPluginManager().addPermission(perm);
		}
		
		return perm;
	}

	@Override
	public void clearPermissions(Player player) {
		PermissionAttachment attach = (PermissionAttachment) this.attachments.remove(player.getUniqueId());
		if (attach != null)
			attach.remove();
		getServer().getPluginManager().removePermission(player.getUniqueId().toString());
	}
	
	public Server getServer() {
		return this.plugin.getServer();
	}
}
