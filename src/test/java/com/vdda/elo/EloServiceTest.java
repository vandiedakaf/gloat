package com.vdda.elo;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by francois
 * on 2017-01-15
 * for vandiedakaf solutions
 */
public class EloServiceTest {

    @Tested
    private EloService eloService;

    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private SlackUtilities slackUtilities;

    @Before
    public void setUp() throws Exception {
        eloService = new EloService(contestRepository, userCategoryRepository, slackUtilities);
    }

    @Test
    public void processContestsGolden() throws Exception {

        new Expectations() {{
            contestRepository.findByProcessedOrderByCreatedAsc(anyBoolean);
            result = mockContests();
        }};

        eloService.processContests();

        new Verifications() {{
            slackUtilities.sendChatMessage("teamId", "channelId", anyString);
        }};
    }

    @Test
    public void newUserCategories() throws Exception {

        new Expectations() {{
            contestRepository.findByProcessedOrderByCreatedAsc(anyBoolean);
            result = mockContest();

            userCategoryRepository.findOne((UserCategoryPK) any);
            result = null;
            result = null;
            result = mockUserCategory();
            result = mockUserCategory();
            result = mockUserCategory();
            result = mockUserCategory();
        }};

        eloService.processContests();

        new Verifications() {{
            userCategoryRepository.save((UserCategory) withNotNull());
            times = 4;
        }};
    }

    private List<Contest> mockContest() {
        List<Contest> contests = new ArrayList<>();

        Category category = new Category("teamId", "channelId");
        User winner = new User("teamId", "winnerId");
        User loser = new User("teamId", "loserId");

        Contest contestA = new Contest(category, winner, loser);
        contestA.setId(1L);
        contestA.setCreated(new Date());
        contests.add(contestA);

        return contests;
    }

    private List<Contest> mockContests() {
        List<Contest> contests = new ArrayList<>();

        Category category = new Category("teamId", "channelId");
        User winner = new User("teamId", "winnerId");
        User loser = new User("teamId", "loserId");

        Contest contestA = new Contest(category, winner, loser);
        contestA.setId(1L);
        contestA.setCreated(new Date());
        contests.add(contestA);

        Contest contestB = new Contest(category, winner, loser);
        contestB.setId(2L);
        contestB.setCreated(new Date());
        contests.add(contestB);

        return contests;
    }

    private UserCategory mockUserCategory() {
        User user = new User("teamId", "userId");
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 1500);
        userCategory.setK(32);

        return userCategory;
    }
}
