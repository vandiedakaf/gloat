package com.vdda.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.Oauth;
import com.vdda.repository.OauthRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2016-12-29
 * for vandiedakaf solutions
 */
public class SlackUtilitiesTest {

    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "123";
    private static final String USER_NAME_AT = "@userName";
    private static final String USER_NAME = "userName";

    @Tested
    SlackUtilities slackUtilities;

    @Mocked
    Slack slack;
    @Injectable
    OauthRepository oauthRepository;

    @Test
    public void userExisting() throws Exception {

        expectationGolden();

        Optional<User> user = slackUtilities.getUser(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getId(), is(USER_ID));
    }

    @Test
    public void userExistingNoAtSymbol() throws Exception {

        expectationGolden();

        Optional<User> user = slackUtilities.getUser(TEAM_ID, USER_NAME);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getId(), is(USER_ID));
    }

    @Test
    public void userNotExist() throws Exception {

        expectationGolden();

        Optional<User> user = slackUtilities.getUser(TEAM_ID, "I don't exist");
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void oauthNotExist() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = null;
        }};

        Optional<User> user = slackUtilities.getUser(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void tokenInvalid(@Mocked Slack slack) throws Exception {

        new Expectations() {{
            slack.methods();
            result = new IOException("test exception");
        }};

        Optional<User> user = slackUtilities.getUser(TEAM_ID, USER_NAME);
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void usersListResponseFail() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().usersList(withNotNull());
            result = mockUsersListResponseFail();
        }};

        Optional<User> user = slackUtilities.getUser(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(false));
    }

    protected void expectationGolden() throws Exception {
        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().usersList(withNotNull());
            result = mockUsersListResponse();
        }};
    }

    private UsersListResponse mockUsersListResponse() {
        UsersListResponse usersListResponse = new UsersListResponse();
        usersListResponse.setOk(true);
        List<User> members = new ArrayList<>();
        User user = new User();
        user.setId(USER_ID);
        user.setName(USER_NAME);
        members.add(user);
        usersListResponse.setMembers(members);
        return usersListResponse;
    }

    private UsersListResponse mockUsersListResponseFail() {
        UsersListResponse usersListResponse = new UsersListResponse();
        usersListResponse.setOk(false);
        return usersListResponse;
    }

}
