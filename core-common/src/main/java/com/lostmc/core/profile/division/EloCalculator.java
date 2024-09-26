package com.lostmc.core.profile.division;

import lombok.Getter;

public class EloCalculator {

    private final double kPower;
    private final int minEloGain;
    private final int maxEloGain;
    private final int minEloLoss;
    private final int maxEloLoss;

    public EloCalculator(double kPower, int minEloGain, int maxEloGain, int minEloLoss, int maxEloLoss) {
        this.kPower = kPower;
        this.minEloGain = minEloGain;
        this.maxEloGain = maxEloGain;
        this.minEloLoss = minEloLoss;
        this.maxEloLoss = maxEloLoss;
    }

    public EloCalculator() {
        this(35.0D, 2, 25, 2, 25);
    }

    public Result calculate(int winnerElo, int loserElo) {
        double winnerQ = Math.pow(10.0D, winnerElo / 300.0D);
        double loserQ = Math.pow(10.0D, loserElo / 300.0D);
        double winnerE = winnerQ / (winnerQ + loserQ);
        double loserE = loserQ / (winnerQ + loserQ);
        int winnerGain = (int)(this.kPower * (1.0D - winnerE));
        int loserGain = (int)(this.kPower * (0.0D - loserE));
        winnerGain = Math.min(winnerGain, this.maxEloGain);
        winnerGain = Math.max(winnerGain, this.minEloGain);
        loserGain = Math.min(loserGain, -this.minEloLoss);
        loserGain = Math.max(loserGain, -this.maxEloLoss);
        return new Result(winnerElo, winnerGain, loserElo, loserGain);
    }

    @Getter
    public static class Result {

        private final int winnerOld;

        private final int winnerGain;

        private final int winnerNew;

        private final int loserOld;

        private final int loserGain;

        private final int loserNew;

        Result(int winnerOld, int winnerGain, int loserOld, int loserGain) {
            this.winnerOld = winnerOld;
            this.winnerGain = winnerGain;
            this.winnerNew = winnerOld + winnerGain;
            this.loserOld = loserOld;
            this.loserGain = loserGain;
            this.loserNew = loserOld + loserGain;
        }
    }
}
