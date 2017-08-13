package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WinProcessorTest {
    @Tested
    WinProcessor winProcessor;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private EnvProperties envProperties;

    @Before
    public void setUp() throws Exception {
        winProcessor = new WinProcessor(envProperties, contestRepository, userCategoryRepository);
    }

    @Test
    public void getRatings(@Mocked EloCalculator eloCalculator) throws Exception {

        winProcessor.getRatings(eloCalculator);

        new Verifications() {{
            eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);
        }};
    }

    @Test
    public void adjustUserCategoryStats() throws Exception {

        UserCategory reporterCategory = new UserCategory(null);
        reporterCategory.setWins(3);

        UserCategory opponentCategory = new UserCategory(null);
        opponentCategory.setLosses(5);

        winProcessor.adjustUserCategoryStats(reporterCategory, opponentCategory);

        assertEquals((Integer) 4, reporterCategory.getWins());
        assertEquals((Integer) 6, opponentCategory.getLosses());
    }
}
