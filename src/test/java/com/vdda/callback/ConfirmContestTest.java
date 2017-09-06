package com.vdda.callback;

import com.vdda.EnvProperties;
import com.vdda.contest.ContestResolver;
import com.vdda.jpa.*;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfirmContestTest {

    private static final String CHANNEL_ID = "channelId";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";

    @Tested
    private ConfirmContest confirmContest;

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
    @Mocked
    private EnvProperties envProperties;

    @Before
    public void setUp() throws Exception {
        confirmContest = new ConfirmContest(envProperties, categoryRepository, userRepository, contestRepository, contestResolver, userCategoryRepository, slackUtilities) {
            @Override
            protected Attachment confirmAttachment(String opponentId) {
                Attachment attachment = new Attachment();
                attachment.setText("testConfirm");
                return attachment;
            }

            @Override
            protected Attachment denyAttachment(String opponentId) {
                Attachment attachment = new Attachment();
                attachment.setText("testDeny");
                return attachment;
            }

            @Override
            protected ContestOutcome getContestOutcome() {
                return null;
            }

            @Override
            protected void notifyChannelBefore(Contest contest) {
                getOrCreateUserCategory(null);
            }

            @Override
            public String getCallbackId() {
                return "callbackId";
            }
        };
    }

    @Test
    public void getCallbackId() throws Exception {
        assertThat(confirmContest.getCallbackId(), is(equalTo("callbackId")));
    }

    @Test
    public void runGoldenNo() throws Exception {
        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("no");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.CONFIRM_VICTORY.toString() + "|" + USER_ID);

        Response response = confirmContest.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("testDeny"));
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
        callbackRequest.setCallbackId(Callbacks.CONFIRM_VICTORY.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = confirmContest.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

        new Verifications() {{
            Contest contest;
            contestRepository.save(contest = withCapture());
            assertThat(contest.getCategory().getChannelId(), is(equalTo(CHANNEL_ID)));
        }};
    }

    @Test
    public void runGoldenYesNewUser() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategory();
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.CONFIRM_VICTORY.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = confirmContest.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

        new Verifications() {{
            Contest contest;
            contestRepository.save(contest = withCapture());
            assertThat(contest.getCategory().getChannelId(), is(equalTo(CHANNEL_ID)));

            com.vdda.jpa.User userSaved;
            userRepository.save(userSaved = withCapture());
            assertThat(userSaved.getUserId(), is(equalTo(USER_ID)));
        }};
    }

    @Test
    public void runGoldenYesNewCategory() throws Exception {

        new Expectations() {{
            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUser();
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.CONFIRM_VICTORY.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = confirmContest.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

        new Verifications() {{
            Category category;
            categoryRepository.save(category = withCapture());
            assertThat(category.getChannelId(), is(equalTo(CHANNEL_ID)));
        }};
    }

    @Test
    public void runGoldenYesNewUserCategory() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategory();

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUser();

            userCategoryRepository.findOne((UserCategoryPK) any);
            result = null;
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.CONFIRM_VICTORY.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = confirmContest.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

        new Verifications() {{
            Contest contest;
            contestRepository.save(contest = withCapture());
            assertThat(contest.getCategory().getChannelId(), is(equalTo(CHANNEL_ID)));
        }};
    }

    private Optional<Category> mockCategory() {
        Category category = new Category(TEAM_ID, CHANNEL_ID);
        category.setId(1L);
        return Optional.of(category);
    }

    private Optional<com.vdda.jpa.User> mockUser() {
        com.vdda.jpa.User user = new com.vdda.jpa.User(TEAM_ID, USER_ID);
        user.setId(1L);
        return Optional.of(user);
    }

}
