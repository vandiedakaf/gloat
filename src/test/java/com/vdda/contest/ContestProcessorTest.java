package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.*;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-02-20
 * for vandiedakaf solutions
 */
public class ContestProcessorTest {

    @Tested
    private ContestProcessor contestProcessor;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private EnvProperties envProperties;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private SlackUtilities slackUtilities;

    @Before
    public void setUp() throws Exception {
        contestProcessor = new ContestProcessor(envProperties, contestRepository, userCategoryRepository, slackUtilities) {
            @Override
            EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
                return eloCalculator.adjustedRating(EloCalculator.Outcome.WIN); // use any outcome
            }

            @Override
            void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
            }
        };
    }

    @Test
    public void processContestGolden() throws Exception {
        Category category = new Category("teamId", "channelId");
        User reporter = new User("teamId", "reporterId");
        User opponent = new User("teamId", "opponentId");

        Contest contest = new Contest(category, reporter, opponent, ContestOutcome.WIN);
        contest.setId(1L);
        contest.setCreated(new Date());

        contestProcessor.processContest(contest);

        new Verifications() {{
            userCategoryRepository.save((UserCategory) withNotNull());
            times = 2;

            Contest contestCapture;
            contestRepository.save(contestCapture = withCapture());
            assertThat(contestCapture.getContestOutcome(), is(equalTo(ContestOutcome.WIN)));
        }};
    }

    @Test
    public void processContestGoldenNewUserCategory() throws Exception {
        new Expectations() {{
            userCategoryRepository.findOne((UserCategoryPK) any);
            result = null;
        }};

        Category category = new Category("teamId", "channelId");
        User reporter = new User("teamId", "reporterId");
        User opponent = new User("teamId", "opponentId");

        Contest contest = new Contest(category, reporter, opponent, ContestOutcome.WIN);
        contest.setId(1L);
        contest.setCreated(new Date());

        contestProcessor.processContest(contest);

        new Verifications() {{
            userCategoryRepository.save((UserCategory) withNotNull());
            times = 4;

            Contest contestCapture;
            contestRepository.save(contestCapture = withCapture());
            assertThat(contestCapture.getContestOutcome(), is(equalTo(ContestOutcome.WIN)));
        }};
    }

}