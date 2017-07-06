package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.SlackUtilities;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by francois
 * on 2017-02-20
 * for vandiedakaf solutions
 */
public class LossProcessorTest {
    @Tested
    LossProcessor lossProcessor;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private SlackUtilities slackUtilities;
    @Mocked
    private EnvProperties envProperties;

    @Before
    public void setUp() throws Exception {
        lossProcessor = new LossProcessor(envProperties, contestRepository, userCategoryRepository, slackUtilities);
    }

    @Test
    public void getRatings(@Mocked EloCalculator eloCalculator) throws Exception {

        lossProcessor.getRatings(eloCalculator);

        new Verifications() {{
            eloCalculator.adjustedRating(EloCalculator.Outcome.LOSE);
        }};
    }

    @Test
    public void adjustUserCategoryStats() throws Exception {
        UserCategory reporterCategory = new UserCategory(null);
        reporterCategory.setLosses(3);

        UserCategory opponentCategory = new UserCategory(null);
        opponentCategory.setWins(5);

        lossProcessor.adjustUserCategoryStats(reporterCategory, opponentCategory);

        assertEquals((Integer) 4, reporterCategory.getLosses());
        assertEquals((Integer) 6, opponentCategory.getWins());
    }
}
