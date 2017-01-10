package com.vdda.elo;

/**
 * Created by francois
 * on 2017-01-02
 * for vandiedakaf solutions
 */
public class Elo {

    private double getExpectedScore(long ratingA, long ratingB) {
        return 1.0 / (1 + Math.pow(10, ((ratingB - ratingA) / 400.0)));
    }

    private double adjustedRating(long rating, double expectedScore, double score, int k) {
        return rating + k * (score - expectedScore);
    }

//    public static void main(String[] args) {
//        Elo elo = new Elo();
//
//        long ratingA = 1500;
//        long ratingB = 1500;
//
//
//        // victory
//        double expectedScore = elo.getExpectedScore(ratingA, ratingB);
//        ratingA = (long) elo.adjustedRating(ratingA, expectedScore, 1, 32);
//        ratingB = (long) elo.adjustedRating(ratingB, 1 - expectedScore, 0, 32);
//        System.out.println("expectedScore = " + expectedScore);
//        System.out.println("ratingA = " + ratingA);
//        System.out.println("ratingB = " + ratingB);
//
//        expectedScore = elo.getExpectedScore(ratingA, ratingB);
//        ratingA = (long) elo.adjustedRating(ratingA, expectedScore, 1, 32);
//        ratingB = (long) elo.adjustedRating(ratingB, 1 - expectedScore, 0, 32);
//        System.out.println("expectedScore = " + expectedScore);
//        System.out.println("ratingA = " + ratingA);
//        System.out.println("ratingB = " + ratingB);
//
//        expectedScore = elo.getExpectedScore(ratingA, ratingB);
//        ratingA = (long) elo.adjustedRating(ratingA, expectedScore, 1, 32);
//        ratingB = (long) elo.adjustedRating(ratingB, 1 - expectedScore, 0, 32);
//        System.out.println("expectedScore = " + expectedScore);
//        System.out.println("ratingA = " + ratingA);
//        System.out.println("ratingB = " + ratingB);
//
//        expectedScore = elo.getExpectedScore(ratingA, ratingB);
//        ratingA = (long) elo.adjustedRating(ratingA, expectedScore, 0, 32);
//        ratingB = (long) elo.adjustedRating(ratingB, 1 - expectedScore, 1, 32);
//        System.out.println("expectedScore = " + expectedScore);
//        System.out.println("ratingA = " + ratingA);
//        System.out.println("ratingB = " + ratingB);
//
////        if result == self.name:
////        self.rating = rating_adj(self.rating, exp_score_a, 1)
////        other.rating = rating_adj(other.rating, 1 - exp_score_a, 0)
////        elif result == other.name:
////        self.rating = rating_adj(self.rating, exp_score_a, 0)
////        other.rating = rating_adj(other.rating, 1 - exp_score_a, 1)
////        elif result == 'Draw':
////        self.rating = rating_adj(self.rating, exp_score_a, 0.5)
////        other.rating = rating_adj(other.rating, 1 - exp_score_a, 0.5)
//    }
}
