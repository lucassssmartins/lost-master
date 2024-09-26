package com.lostmc.hungergames.scheduler;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.ScheduleArgs;
import com.lostmc.hungergames.sidebar.GameSidebarModel;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class InvincibilityScheduler implements Schedule {

	@Override
	public void pulse(ScheduleArgs args) {
		if (args.getStage() != GameStage.INVINCIBILITY)
			return;
		if (args.getTimer() <= 0) {
			((HungerGames) HungerGames.getInstance()).setGameStage(GameStage.GAMETIME);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
				Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard(p);
				scoreboard.setModel(new GameSidebarModel(scoreboard));
			}
			((HungerGamesMode) ((HungerGames) HungerGames.getInstance()).getGameMode()).checkWinner();
			return;
		}
		if (args.getTimer() <= 5) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1f, 1f);
			}
		}
		if ((args.getTimer() % 60 == 0 || (args.getTimer() < 60 && (args.getTimer() % 15 == 0 || args.getTimer() == 10 || args.getTimer() <= 5)))) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(MessageManager.getCountDownMessage(MessageManager.CountDownMessageType.INVINCIBILITY, args.getTimer()));
			}
		}
	}
}
