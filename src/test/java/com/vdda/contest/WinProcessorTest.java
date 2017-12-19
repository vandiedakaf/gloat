package com.vdda.contest;

import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.jpa.UserUserCategory;
import com.vdda.domain.repository.ContestRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserUserCategoryRepository;
import com.vdda.elo.EloCalculator;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WinProcessorTest {

	@Autowired
	WinProcessor winProcessor;

	@Mocked
	private ContestRepository contestRepository;
	@Mocked
	private UserCategoryRepository userCategoryRepository;
	@Mocked
	private UserUserCategoryRepository userUserCategoryRepository;

	@Before
	public void setUp() throws Exception {
		winProcessor = new WinProcessor(contestRepository, userCategoryRepository, userUserCategoryRepository);
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

	@Test
	public void adjustUserUserCategoryStats() throws Exception {
		UserUserCategory userUserCategoryReporter = new UserUserCategory(null);
		userUserCategoryReporter.setWins(3);

		UserUserCategory userUserCategoryOpponent = new UserUserCategory(null);
		userUserCategoryOpponent.setLosses(5);

		winProcessor.adjustUserUserCategoryStats(userUserCategoryReporter, userUserCategoryOpponent);

		assertEquals((Integer) 4, userUserCategoryReporter.getWins());
		assertEquals((Integer) 6, userUserCategoryOpponent.getLosses());
	}
}
