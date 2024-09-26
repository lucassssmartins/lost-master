package com.lostmc.hungergames.constructor;

import com.lostmc.game.GameType;
import com.lostmc.hungergames.stage.GameStage;

public class ScheduleArgs {

	private GameType gameType;
	private GameStage stage;
	private int timer;

	public ScheduleArgs(GameType gameType, GameStage stage, int timer) {
		this.gameType = gameType;
		this.stage = stage;
		this.timer = timer;
	}

	public GameType getGameType() {
		return gameType;
	}

	public GameStage getStage() {
		return stage;
	}

	public int getTimer() {
		return timer;
	}
}
