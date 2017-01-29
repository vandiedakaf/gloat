package com.vdda.elo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by francois
 * on 2017-01-02
 * for vandiedakaf solutions
 */
public class EloCalculator {

    private static final double FOUR_HUNDRED = 400;

    private int playerRating;
    private int opponentRating;
    private int k;

    public EloCalculator(int playerRating, int opponentRating, int k) {
        this.playerRating = playerRating;
        this.opponentRating = opponentRating;
        this.k = k;
    }

    // Code taken from http://chess.stackexchange.com/a/1379
    // Also see "Theory" section https://en.wikipedia.org/wiki/Elo_rating_system
    private double getExpectedScorePlayer() {
        return 1.0 / (1 + Math.pow(10, (Math.min((opponentRating - playerRating),400) / FOUR_HUNDRED)));
    }

    private double getExpectedScoreOpponent() {
        return 1.0 / (1 + Math.pow(10, (Math.min((playerRating - opponentRating),400) / FOUR_HUNDRED)));
    }

    public Ratings adjustedRating(Outcome outcome) {
        Ratings ratings = new Ratings();
        ratings.setPlayerRating(playerRating + k * (outcome.getValue() - getExpectedScorePlayer()));

        ratings.setOpponentRating(opponentRating + k * ((1 - outcome.getValue()) - getExpectedScoreOpponent()));
        return ratings;
    }

    public enum Outcome {
        WIN(1),
        LOSE(0),
        DRAW(0.5);

        private double value;

        public double getValue() {
            return value;
        }

        Outcome(double value) {
            this.value = value;
        }

    }

    @Getter
    @Setter
    public class Ratings {
        private double playerRating;
        private double opponentRating;
    }
}
