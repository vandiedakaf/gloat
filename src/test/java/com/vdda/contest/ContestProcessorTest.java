package com.vdda.contest;

import com.vdda.domain.jpa.*;
import com.vdda.domain.repository.ContestRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserUserCategoryRepository;
import com.vdda.elo.EloCalculator;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContestProcessorTest {

	private ContestProcessor contestProcessor;

	@Mocked
	private ContestRepository contestRepository;
	@Mocked
	private UserCategoryRepository userCategoryRepository;
	@Mocked
	private UserUserCategoryRepository userUserCategoryRepository;

	@Before
	public void setUp() throws Exception {
		contestProcessor = new ContestProcessor(contestRepository, userCategoryRepository, userUserCategoryRepository) {
			@Override
			EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
				return eloCalculator.adjustedRating(EloCalculator.Outcome.WIN); // use any outcome
			}

			@Override
			void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
			}

			@Override
			void adjustUserUserCategoryStats(UserUserCategory userUserCategoryReporter, UserUserCategory userUserCategoryOpponent) {

			}
		};
	}

	@Test
	public void processContestGolden() throws Exception {
		contestProcessor.processContest(createContest());

		new Verifications() {{
			userCategoryRepository.save((UserCategory) withNotNull());
			times = 2;

			Contest contestCapture;
			contestRepository.save(contestCapture = withCapture());
			assertThat(contestCapture.getContestOutcome(), is((ContestOutcome.WIN)));
		}};
	}

	@Test
	public void processContestGoldenNewUserCategory() throws Exception {
		new Expectations() {{
			userCategoryRepository.findOne((UserCategoryPK) any);
			result = null;
		}};

		contestProcessor.processContest(createContest());

		new Verifications() {{
			userCategoryRepository.save((UserCategory) withNotNull());
			times = 4;

			Contest contestCapture;
			contestRepository.save(contestCapture = withCapture());
			assertThat(contestCapture.getContestOutcome(), is((ContestOutcome.WIN)));
		}};
	}

	@Test
	public void processContestGoldenNewUserUserCategory() throws Exception {
		new Expectations() {{
			userUserCategoryRepository.findOne((UserUserCategoryPK) any);
			result = null;
		}};

		Contest contest = createContest();

		contestProcessor.processContest(contest);

		new Verifications() {{
			userUserCategoryRepository.save((UserUserCategory) withNotNull());
			times = 4;

			Contest contestCapture;
			contestRepository.save(contestCapture = withCapture());
			assertThat(contestCapture.getContestOutcome(), is((ContestOutcome.WIN)));
		}};
	}

	private Contest createContest() {
		Category category = new Category("teamId", "channelId");
		User reporter = new User("teamId", "reporterId");
		User opponent = new User("teamId", "opponentId");

		Contest contest = new Contest(category, reporter, opponent, ContestOutcome.WIN);
		contest.setId(1L);
		contest.setCreated(new Date());
		return contest;
	}

}
