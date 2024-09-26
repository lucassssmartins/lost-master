package com.lostmc.hungergames.scheduler;

import java.util.Random;

import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.ScheduleArgs;
import com.lostmc.hungergames.listener.DeathListener;
import com.lostmc.hungergames.manager.AssistManager;
import com.lostmc.hungergames.manager.FeastManager;
import com.lostmc.hungergames.status.StatusHandler;
import com.lostmc.hungergames.structure.FinalBattleStructure;
import com.lostmc.hungergames.structure.MinifeastStructure;
import com.lostmc.hungergames.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameScheduler implements Schedule {

	private static FeastManager feastManager;
	private MinifeastStructure minifeast;
	private FinalBattleStructure finalBattle;
	private int nextMinifeast;
	private Random r = new Random();

	public GameScheduler() {
		feastManager = Management.getManagement(FeastManager.class);
		minifeast = new MinifeastStructure();
		finalBattle = new FinalBattleStructure();
		nextMinifeast = 240 + r.nextInt(300);
	}

	public static void killPlayer(Player p) {
		HungerGamer gamer = HungerGamer.getGamer(p);
		CombatLog combatLog = gamer.getCombatLog();
		if (combatLog.isLogged()) {
			Player killer = Bukkit.getPlayer(combatLog.getCombatLogged());
			if (killer != null) {
				p.damage(p.getMaxHealth(), killer);
				return;
			}
		}
		if (p.hasPermission("core.cmd.admin"))
			gamer.setGamerState(HungerGamer.GamerState.GAMEMAKER);
		else
			gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
		Bukkit.broadcastMessage("§b" + p.getName() + gamer.getKits().values()
				+ " morreu e foi eliminado.");
		((HungerGamesMode) ((HungerGames) HungerGames.getInstance()).getGameMode()).checkWinner();
	}

	@Override
	public void pulse(ScheduleArgs args) {
		if (args.getTimer() == 60 * 60) {
			Player p = null;
			int kills = 0;
			for (Player player : Bukkit.getOnlinePlayers()) {
				HungerGamer gamer = HungerGamer.getGamer(player);
				if (gamer.isNotPlaying())
					continue;
				if (gamer.getMatchKills() >= kills) {
					if (p != null) {
						killPlayer(p);
					}
					kills = gamer.getMatchKills();
					p = player;
				} else {
					killPlayer(player);
				}
			}
			((HungerGamesMode) ((HungerGames) HungerGames.getInstance()).getGameMode()).checkWinner();
		}
		/**
		 * FEAST
		 */
		feastManager.onTimer(args.getTimer());
		/**
		 * MINI FEAST
		 */
		if (nextMinifeast <= 0) {
			nextMinifeast = 240 + r.nextInt(300);
			Location place = minifeast.findPlace();
			minifeast.place(place);
			Bukkit.broadcastMessage(MessageManager.getMinifeastMessage(place));
			feastManager.incrementMiniFeastCount();
		} else {
			--nextMinifeast;
		}
		/**
		 * BONUS FEAST
		 */
		if (args.getTimer() == HungerGamesMode.BONUSFEAST_SPAWN) {
			if (feastManager.isBonusFeast() && !feastManager.isBonusFeastSpawned()) {
				feastManager.spawnBonusFeast();
				Bukkit.broadcastMessage("§cO feast bônus spawnou em algum lugar do mapa!");
			}
		}
		/**
		 * FINAL BATTLE
		 */
		if (args.getTimer() == HungerGamesMode.FINALBATTLE_TIME) {
			Location loc = finalBattle.findPlace();
			finalBattle.place(loc);
			finalBattle.teleportPlayers(loc);
			Bukkit.broadcastMessage("§cA batalha final começou!");
		}
	}

	public static FeastManager getFeastManager() {
		return feastManager;
	}
}
