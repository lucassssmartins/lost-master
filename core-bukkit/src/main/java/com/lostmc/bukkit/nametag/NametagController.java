package com.lostmc.bukkit.nametag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.nametag.packet.PacketWrapper;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketInUpdateTag;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class NametagController extends Controller implements Listener {

	private final HashMap<String, TeamInfo> TEAMS = new HashMap<>();
	private final HashMap<String, TeamInfo> CACHED_TEAM_INFOS = new HashMap<>();
	private final List<String> CHARS = new ArrayList<>();

	public NametagController(Control control) {
		super(control);
	}

	@Override
	public void onEnable() {
		for (int i = 0; i < 10; i++)
			CHARS.add(String.valueOf(i));
		for (char c = 'A'; c <= 'Z'; c++)
			CHARS.add(String.valueOf(c));
		for (char c = 'a'; c <= 'z'; c++)
			CHARS.add(String.valueOf(c));
		registerListener(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void join(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		ScoreboardHandler.getInstance().newScoreboard(p);
		sendTeams(p);
		setNametag(p, Profile.getProfile(p).getTag(), false);
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
	public void quit(PlayerQuitEvent event) {
		reset(event.getPlayer().getName());

		com.lostmc.bukkit.api.scoreboard.Scoreboard scoreboard =
				ScoreboardHandler.getInstance().removeScoreboard(event.getPlayer());
		scoreboard.unregister();

		ScoreboardManager boardManager = getServer().getScoreboardManager();
		try {
			Field javaField = boardManager.getClass().getDeclaredField("scoreboards");
			javaField.setAccessible(true);
			Collection<Scoreboard> scoreboards = (Collection<Scoreboard>) javaField.get(boardManager);
			Player player = event.getPlayer();
			Scoreboard board = player.getScoreboard();
			if (board != null)
				scoreboards.remove(board);
			player.setScoreboard(boardManager.getMainScoreboard());
		} catch (Exception e) {
			e.printStackTrace();
		}
		event.getPlayer().setScoreboard(boardManager.getMainScoreboard());
	}

	@Override
	public void onDisable() {
		for (Player online : getServer().getOnlinePlayers())
			reset(online.getName());
		TEAMS.clear();
		CACHED_TEAM_INFOS.clear();
	}

	private TeamInfo getFakeTeam(String name, String prefix, String suffix) {
		for (TeamInfo fakeTeam : TEAMS.values()) {
			if (fakeTeam.isSimilar(name, prefix, suffix)) {
				return fakeTeam;
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	private void addPlayerToTeam(String name, String player, String prefix, String suffix) {
		TeamInfo previous = getFakeTeam(player);

		if (previous != null && previous.isSimilar(name, prefix, suffix)) {
			return;
		}

		reset(player);

		TeamInfo joining = getFakeTeam(name, prefix, suffix);
		if (joining != null) {
			joining.addMember(player);
		} else {
			joining = new TeamInfo(name, prefix, suffix);
			joining.addMember(player);
			TEAMS.put(joining.getName(), joining);
			addTeamPackets(joining);
		}

		Player adding = Bukkit.getPlayerExact(player);
		if (adding != null) {
			addPlayerToTeamPackets(joining, adding.getName());
			cache(adding.getName(), joining);
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
			addPlayerToTeamPackets(joining, offlinePlayer.getName());
			cache(offlinePlayer.getName(), joining);
		}
	}

	public TeamInfo reset(String player) {
		return reset(player, decache(player));
	}

	private TeamInfo reset(String player, TeamInfo fakeTeam) {
		if (fakeTeam != null && fakeTeam.getMembers().remove(player)) {
			boolean delete;
			Player removing = Bukkit.getPlayerExact(player);
			if (removing != null) {
				delete = removePlayerFromTeamPackets(fakeTeam, removing.getName());
			} else {
				@SuppressWarnings("deprecation")
				OfflinePlayer toRemoveOffline = Bukkit.getOfflinePlayer(player);
				delete = removePlayerFromTeamPackets(fakeTeam, toRemoveOffline.getName());
			}

			if (delete) {
				removeTeamPackets(fakeTeam);
				TEAMS.remove(fakeTeam.getName());
			}
		}

		return fakeTeam;
	}

	private TeamInfo decache(String player) {
		return CACHED_TEAM_INFOS.remove(player);
	}

	public TeamInfo getFakeTeam(String player) {
		return CACHED_TEAM_INFOS.get(player);
	}

	private void cache(String player, TeamInfo fakeTeam) {
		CACHED_TEAM_INFOS.put(player, fakeTeam);
	}

	public String getTeamName(Tag tag) {
		return CHARS.get(tag.ordinal());
	}

	public void setNametag(Player player, Tag tag, boolean save) {
		Profile account = Profile.getProfile(player);

		account.setTag(tag);
		if (save) {
			account.save();
		}

		setNametag((getTeamName(tag) + player.getUniqueId().toString()).substring(0, 16), player.getName(),
				tag.getPrefix(), "");

		if (save) {
			Commons.getRedisBackend().publish(PacketType.UPDATE_IN_TAG.toString(),
					Commons.getGson().toJson(new PacketInUpdateTag(player.getUniqueId(), tag)));
		}
	}

	public void setNametag(Player player, Tag tag) {
		setNametag(player, tag, true);
	}

	public void setNametag(String name, String player, String prefix, String suffix) {
		addPlayerToTeam(name, player, prefix != null ? prefix : "", suffix != null ? suffix : "");
	}

	void sendTeams(Player player) {
		for (TeamInfo fakeTeam : TEAMS.values()) {
			new PacketWrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 0, fakeTeam.getMembers(),
					fakeTeam.isVisible()).send(player);
		}
	}

	void reset() {
		for (TeamInfo fakeTeam : TEAMS.values()) {
			removePlayerFromTeamPackets(fakeTeam, fakeTeam.getMembers());
			removeTeamPackets(fakeTeam);
		}
		CACHED_TEAM_INFOS.clear();
		TEAMS.clear();
	}

	private void removeTeamPackets(TeamInfo fakeTeam) {
		new PacketWrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 1, new ArrayList<>(),
				fakeTeam.isVisible()).send();
	}

	private boolean removePlayerFromTeamPackets(TeamInfo fakeTeam, String... players) {
		return removePlayerFromTeamPackets(fakeTeam, Arrays.asList(players));
	}

	private boolean removePlayerFromTeamPackets(TeamInfo fakeTeam, List<String> players) {
		new PacketWrapper(fakeTeam.getName(), 4, players).send();
		fakeTeam.getMembers().removeAll(players);
		return fakeTeam.getMembers().isEmpty();
	}

	private void addTeamPackets(TeamInfo fakeTeam) {
		new PacketWrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 0, fakeTeam.getMembers(),
				fakeTeam.isVisible()).send();
	}

	private void addPlayerToTeamPackets(TeamInfo fakeTeam, String player) {
		new PacketWrapper(fakeTeam.getName(), 3, Collections.singletonList(player)).send();
	}
}