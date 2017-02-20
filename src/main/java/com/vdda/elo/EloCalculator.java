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

    private int reporterRating;
    private int opponentRating;
    private int k;

    public EloCalculator(int reporterRating, int opponentRating, int k) {
        this.reporterRating = reporterRating;
        this.opponentRating = opponentRating;
        this.k = k;
    }

    // Code taken from http://chess.stackexchange.com/a/1379
    // Also see "Theory" section https://en.wikipedia.org/wiki/Elo_rating_system
    private double getExpectedScoreReporter() {
        return 1.0 / (1.0 + Math.pow(10, (opponentRating - reporterRating) / FOUR_HUNDRED));
    }

    private double getExpectedScoreOpponent() {
        return 1.0 / (1.0 + Math.pow(10, (reporterRating - opponentRating) / FOUR_HUNDRED));
    }

    public Ratings adjustedRating(Outcome outcome) {
        Ratings ratings = new Ratings();
        double expectedScoreReporter = getExpectedScoreReporter();
        ratings.setReporterRating(reporterRating + k * (outcome.getValue() - expectedScoreReporter));

        double expectedScoreOpponent = getExpectedScoreOpponent();
        ratings.setOpponentRating(opponentRating + k * ((1 - outcome.getValue()) - expectedScoreOpponent));
        return ratings;
    }

    public static Integer determineK(int contestTotal) {
        if (contestTotal >= 50) {
            return 16;
        } else if (contestTotal >= 10) {
            return 24;
        } else
            return 32;
    }

    public enum Outcome {
        WIN(1),
        LOSE(0),
        DRAW(0.5);

        private double value;

        Outcome(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

    }

    @Getter
    @Setter
    public class Ratings {
        private double reporterRating;
        private double opponentRating;
    }
}
