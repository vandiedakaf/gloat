package com.vdda.callback;

import com.vdda.contest.ContestResolver;
import com.vdda.domain.jpa.Category;
import com.vdda.domain.jpa.Contest;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.ContestRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vdda.callback.Callbacks.CONFIRM_SERIES;
import static com.vdda.command.service.CallbackBuilder.callbackIdBuilder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmContestSeriesTest {

    private static final String CHANNEL_ID = "channelId";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";
    private static final String OUTCOME = "wld";

	@Autowired
    private ConfirmContestSeries confirmContestNewSeries;

    @Mocked
    private CategoryRepository categoryRepository;
    @Mocked
    private UserRepository userRepository;
    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private ContestResolver contestResolver;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private SlackUtilities slackUtilities;

    @Before
    public void setUp() throws Exception {
        confirmContestNewSeries = new ConfirmContestSeries(categoryRepository, userRepository, contestRepository, contestResolver, userCategoryRepository, slackUtilities);
    }

    @Test
    public void getCallbackId() throws Exception {
        assertThat(confirmContestNewSeries.getCallbackId(), is(Callbacks.CONFIRM_SERIES.toString()));
    }

    @Test
    public void runGoldenNo() throws Exception {
        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("no");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(callbackIdBuilder(CONFIRM_SERIES.toString(),USER_ID));

        Response response = confirmContestNewSeries.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("You've opted not to confirm this series."));
    }

    @Test
    public void runGoldenYes() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategory();

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUser();
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID, OUTCOME));
        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = confirmContestNewSeries.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("You've confirmed this series."));

        new Verifications() {{
            Contest contest;
            contestRepository.save(contest = withCapture());
            assertThat(contest.getCategory().getChannelId(), is((CHANNEL_ID)));
        }};
    }

    private Optional<Category> mockCategory() {
        Category category = new Category(TEAM_ID, CHANNEL_ID);
        category.setId(1L);
        return Optional.of(category);
    }

    private Optional<com.vdda.domain.jpa.User> mockUser() {
        com.vdda.domain.jpa.User user = new com.vdda.domain.jpa.User(TEAM_ID, USER_ID);
        user.setId(1L);
        return Optional.of(user);
    }

}
