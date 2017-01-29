package com.vdda.elo;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-14
 * for vandiedakaf solutions
 */
public class EloCalculatorTest {
    private static final int ratingA = 1500;
    private static final int ratingB = 1700;
    private static final int k = 32;

    @Test
    public void adjustedRatingWin() throws Exception {
        EloCalculator eloCalculator = new EloCalculator(ratingA, ratingA, k);

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);

        assertThat(ratings.getPlayerRating(), is(closeTo(1516, 0.00001)));
        assertThat(ratings.getOpponentRating(), is(closeTo(1484, 0.00001)));
    }

    @Test
    public void adjustedRatingWin2() throws Exception {
        EloCalculator eloCalculator = new EloCalculator(ratingA, ratingB, k);

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);

        assertThat(ratings.getPlayerRating(), is(closeTo(1524.3, 0.1)));
        assertThat(ratings.getOpponentRating(), is(closeTo(1675.7, 0.1)));
    }

    @Test
    public void adjustedRatingLose() throws Exception {
        EloCalculator eloCalculator = new EloCalculator(ratingA, ratingA, k);

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.LOSE);

        assertThat(ratings.getPlayerRating(), is(closeTo(1484, 0.00001)));
        assertThat(ratings.getOpponentRating(), is(closeTo(1516, 0.00001)));
    }

    @Test
    public void adjustedRatingLose2() throws Exception {
        EloCalculator eloCalculator = new EloCalculator(ratingA, ratingB, k);

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.LOSE);

        assertThat(ratings.getPlayerRating(), is(closeTo(1492.3, 0.1)));
        assertThat(ratings.getOpponentRating(), is(closeTo(1707.7, 0.1)));
    }

    @Test
    public void adjustedRatingDraw() throws Exception {
        EloCalculator eloCalculator = new EloCalculator(ratingA, ratingB, k);

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.DRAW);

        assertThat(ratings.getPlayerRating(), is(closeTo(1508.3, 0.1)));
        assertThat(ratings.getOpponentRating(), is(closeTo(1691.7, 0.1)));
    }
}
