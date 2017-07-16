package com.vdda.contest;

import com.vdda.jpa.Category;
import com.vdda.jpa.Contest;
import com.vdda.jpa.ContestOutcome;
import com.vdda.jpa.User;
import com.vdda.repository.ContestRepository;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-15
 * for vandiedakaf solutions
 */
public class ContestResolverTest {

    @Tested
    private ContestResolver contestResolver;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private ContestProcessorFactory contestProcessorFactory;

    @Before
    public void setUp() throws Exception {
        contestResolver = new ContestResolver(contestRepository, contestProcessorFactory);
    }

    @Test
    public void processContestsGolden() throws Exception {

        new Expectations() {{
            contestRepository.findByProcessedOrderByCreatedAsc(anyBoolean);
            result = mockContests();
        }};

        contestResolver.processContests();

        new Verifications() {{
            ContestOutcome contestOutcome;
            contestProcessorFactory.getContestProcessor(contestOutcome = withCapture());

            assertThat(contestOutcome, is(equalTo(ContestOutcome.WIN)));
        }};
    }

    private List<Contest> mockContests() {
        List<Contest> contests = new ArrayList<>();

        Category category = new Category("teamId", "channelId");
        User reporter = new User("teamId", "reporterId");
        User opponent = new User("teamId", "opponentId");

        Contest contestA = new Contest(category, reporter, opponent, ContestOutcome.WIN);
        contestA.setId(1L);
        contestA.setCreated(new Date());
        contests.add(contestA);

        Contest contestB = new Contest(category, reporter, opponent, ContestOutcome.WIN);
        contestB.setId(2L);
        contestB.setCreated(new Date());
        contests.add(contestB);

        return contests;
    }
}
