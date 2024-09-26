package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.chat.ChatController;
import com.lostmc.bukkit.chat.ChatState;
import com.lostmc.bukkit.event.chat.PlayerChatResponseEvent;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.api.cooldown.CooldownAPI;
import com.lostmc.bukkit.api.cooldown.event.CooldownDisplayEvent;
import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.medal.Medal;
import com.lostmc.core.translate.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.Callable;

public class ChatListener extends BukkitListener {

	private final String CHAT_COOLDOWN_NAME = "chat-speak-cooldown";

	@EventHandler(priority = EventPriority.LOWEST)
	public void cooldown(CooldownDisplayEvent event) {
		if (event.getCooldown().getName().equals(CHAT_COOLDOWN_NAME)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void chat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		Profile account = Profile.getProfile(p);
		ChatController manager = BukkitPlugin.getControl().getController(ChatController.class);

		if (manager.getChatState() == ChatState.DISABLED) {
			if (!p.hasPermission("core.chat.talking.talk-disabled")) {
				event.setCancelled(true);
				p.sendMessage(Translator.tl(account.getLocale(), "messages.chat.chat-disabled"));
				return;
			}
		}

		if (!account.getData(DataType.CHAT).getAsBoolean()) {
			event.setCancelled(true);
			p.sendMessage(Translator.tl(account.getLocale(), "messages.chat.ignoring-all"));
			return;
		}

		if (!p.hasPermission("core.chat.talking.no-delay")) {
			Cooldown cooldown = CooldownAPI.getCooldown(p, CHAT_COOLDOWN_NAME);
			if (cooldown != null) {
				event.setCancelled(true);
				p.sendMessage(Translator.tl(account.getLocale(), "messages.chat.in-cooldown",
						String.format("%.1fs", cooldown.getRemaining())));
				return;
			}
			CooldownAPI.addCooldown(p, new Cooldown(CHAT_COOLDOWN_NAME, 2L));
		}

		Callable<PlayerChatResponseEvent> callback = () -> {
			String message = manager.compileMessage(event.getMessage().replace("%", "%%"));

			if (p.hasPermission("core.chat.advantages.color"))
				message = ChatColor.translateAlternateColorCodes('&', message);

			PlayerChatResponseEvent e = new PlayerChatResponseEvent(p, message);
			Bukkit.getPluginManager().callEvent(e);
			return e;
		};

		try {
			PlayerChatResponseEvent response = callback.call();
			if (!response.isCancelled()) {
				if (response.getFormat() == null || response.getFormat().isEmpty()) {
					Medal medal = account.getMedal();
					String medalFormat = medal == null ? "" : medal.getColor() + medal.getSymbol() + " ";

					// Clan clan = Clan.getClan(profile.getClanId());
					// String clanstr = plugin.isDisplayingClan() && clan != null &&
					// profile.getBoolean(DataType.DISPLAY_CLAN_ABBREVIATION)
					/// ? clan.getColor() + "[" + clan.getAbbreviation() + "] "
					// : "";

					//League ranking = account.getLeague();
					//String rankingFormat = "§7(" + ranking.getColor() + ranking.getSymbol() + "§7) ";

					String tagFormat = account.getTag().getPrefix() + p.getName() + " ";

					// response.setFormat(medalstr + clanstr + rankingstr + nametagstr + "§8» §f" +
					// response.getMessage());
					response.setFormat(medalFormat + tagFormat + "§8» §f" + response.getMessage());
				}

				event.getRecipients()
						.removeIf(recipient -> !Profile.getProfile(recipient)
								.getData(DataType.CHAT).getAsBoolean());

				event.setFormat(response.getFormat());
			} else {
				event.setCancelled(true);
			}
		} catch (Exception e) {
			event.setCancelled(true);
			e.printStackTrace();
			p.sendMessage(e.toString());
		}
	}
}
