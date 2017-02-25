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

/**
 * Created by francois
 * on 2017-02-20
 * for vandiedakaf solutions
 */
public class DrawProcessorTest {
    @Tested
    DrawProcessor drawProcessor;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private SlackUtilities slackUtilities;

    @Mocked
    private UserCategory reporterCategory;
    @Mocked
    private UserCategory opponentCategory;
    @Mocked
    private EnvProperties envProperties;

    @Before
    public void setUp() throws Exception {
        drawProcessor = new DrawProcessor(envProperties, contestRepository, userCategoryRepository, slackUtilities);
    }

    @Test
    public void getRatings(@Mocked EloCalculator eloCalculator) throws Exception {

        drawProcessor.getRatings(eloCalculator);

        new Verifications() {{
            eloCalculator.adjustedRating(EloCalculator.Outcome.DRAW);
        }};
    }

    @Test
    public void adjustUserCategoryStats() throws Exception {

        drawProcessor.adjustUserCategoryStats(reporterCategory, opponentCategory);

        new Verifications() {{
            reporterCategory.setDraws(1);
            opponentCategory.setDraws(1);
        }};
    }
}
