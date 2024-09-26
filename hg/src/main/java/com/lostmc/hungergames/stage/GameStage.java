package com.lostmc.hungergames.stage;

public enum GameStage {

    NONE, //
    WAITING(TimerType.STOP, 300), //
    PREGAME(TimerType.COUNTDOWN, 300), //
    STARTING(TimerType.COUNTDOWN, 15), //
    INVINCIBILITY(TimerType.COUNTDOWN, 120), //
    GAMETIME(TimerType.COUNT_UP, INVINCIBILITY.getDefaultTimer()), //
    ENDING;

    private int defaultTimer;
    private TimerType defaultType;

    GameStage() {
        this(TimerType.STOP, 0);
    }

    GameStage(TimerType type, int timer) {
        defaultType = type;
        defaultTimer = timer;
    }

    public TimerType getDefaultType() {
        return defaultType;
    }

    public int getDefaultTimer() {
        return defaultTimer;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isPregame() {
        return this == WAITING || this == PREGAME || this == STARTING;
    }

    public boolean isInvincibility() {
        return this == INVINCIBILITY;
    }

    public boolean isGametime() {
        return this == GAMETIME;
    }

    public boolean isEnding() {
        return this == ENDING;
    }
}
