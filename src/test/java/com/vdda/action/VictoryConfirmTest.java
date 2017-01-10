package com.vdda.action;

import com.vdda.jpa.Category;
import com.vdda.jpa.Contest;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Response;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-03
 * for vandiedakaf solutions
 */
public class VictoryConfirmTest {

    private static final String CHANNEL_ID = "channelId";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";

    @Tested
    private VictoryConfirm victoryConfirm;

    @Mocked
    private CategoryRepository categoryRepository;
    @Mocked
    private UserRepository userRepository;
    @Mocked
    private ContestRepository contestRepository;
    @Mocked
    private UserCategoryRepository userCategoryRepository;

    @Before
    public void setUp() throws Exception {
        victoryConfirm = new VictoryConfirm(categoryRepository, userCategoryRepository, userRepository, contestRepository);
    }

    @Test
    public void getCallbackId() throws Exception {
        assertThat(victoryConfirm.getCallbackId(), is(Callbacks.VICTORY_CONFIRM.toString()));
    }

    @Test
    public void runGoldenNo() throws Exception {
        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("no");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.VICTORY_CONFIRM.toString() + "|" + USER_ID);

        Response response = victoryConfirm.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("You've opted not to confirm this victory."));
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
        callbackRequest.setCallbackId(Callbacks.VICTORY_CONFIRM.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = victoryConfirm.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("Congratulations on your victory!"));

        new Verifications() {{
            contestRepository.save((Contest) withNotNull());
        }};
    }

    @Test
    public void runGoldenNewCategory() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = null;

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUser();
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.VICTORY_CONFIRM.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = victoryConfirm.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("Congratulations on your victory!"));

        new Verifications() {{
            categoryRepository.save((Category) withNotNull());
            contestRepository.save((Contest) withNotNull());
        }};
    }

    @Test
    public void runGoldenNewUser() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategory();

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = null;
        }};

        CallbackRequest callbackRequest = new CallbackRequest();
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setName("yes");
        actions.add(action);
        callbackRequest.setActions(actions);
        callbackRequest.setCallbackId(Callbacks.VICTORY_CONFIRM.toString() + "|" + USER_ID);

        User user = new User();
        user.setId(USER_ID);
        callbackRequest.setUser(user);

        Channel channel = new Channel();
        channel.setId(CHANNEL_ID);
        callbackRequest.setChannel(channel);

        Team team = new Team();
        team.setId(TEAM_ID);
        callbackRequest.setTeam(team);

        Response response = victoryConfirm.run(callbackRequest);
        assertThat(response.getAttachments().get(0).getText(), containsString("Congratulations on your victory!"));

        new Verifications() {{
            contestRepository.save((Contest) withNotNull());
            userCategoryRepository.save((UserCategory) withNotNull());
            userRepository.save((com.vdda.jpa.User) withNotNull());
        }};
    }

    private Category mockCategory() {
        Category category = new Category(TEAM_ID, CHANNEL_ID);
        category.setId(1L);
        return category;
    }

    private com.vdda.jpa.User mockUser() {
        com.vdda.jpa.User user = new com.vdda.jpa.User(TEAM_ID, USER_ID);
        user.setId(1L);
        return user;
    }

}
