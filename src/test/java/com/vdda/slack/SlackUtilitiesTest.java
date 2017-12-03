package com.vdda.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
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

public class SlackUtilitiesTest {

    private static final String TEAM_ID = "teamId";
    private static final String CHANNEL_ID = "channelId";
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

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getId(), is(USER_ID));
    }

    @Test
    public void userExistingNoAtSymbol() throws Exception {

        expectationGolden();

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, USER_NAME);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getId(), is(USER_ID));
    }

    @Test
    public void userNotExist() throws Exception {

        expectationGolden();

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, "I don't exist");
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void oauthNotExist() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = null;
        }};

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void tokenInvalid(@Mocked Slack slack) throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().usersList((UsersListRequest) any);
            result = new IOException("test exception");
        }};

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, USER_NAME);
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

        Optional<User> user = slackUtilities.getUserByUsername(TEAM_ID, USER_NAME_AT);
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void sendChatMessageGolden() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().chatPostMessage(withNotNull());
            result = mockChatPostMessageResponse();
        }};

        boolean messageSent = slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, "message");
        assertThat(messageSent, is(true));
    }

    @Test
    public void sendChatMessageNoToken() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = null;
        }};

        boolean messageSent = slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, "message");
        assertThat(messageSent, is(false));
    }

    @Test
    public void sendChatMessageException() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().chatPostMessage((ChatPostMessageRequest) any);
            result = new IOException("test exception");
        }};

        boolean messageSent = slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, "message");
        assertThat(messageSent, is(false));
    }

    @Test
    public void sendChatMessageRequestFail() throws Exception {

        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().chatPostMessage(withNotNull());
            result = mockChatPostMessageResponseFail();
        }};

        boolean messageSent = slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, "message");
        assertThat(messageSent, is(false));
    }

    private void expectationGolden() throws Exception {
        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods().usersList(withNotNull());
            result = mockUsersListResponse();
        }};
    }

    private void expectationException() throws Exception {
        new Expectations() {{
            oauthRepository.findOne(TEAM_ID);
            result = new Oauth(TEAM_ID, "accessToken");

            slack.methods();
            result = new IOException("test exception");
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

    private ChatPostMessageResponse mockChatPostMessageResponse() {
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(true);
        return chatPostMessageResponse;
    }

    private ChatPostMessageResponse mockChatPostMessageResponseFail() {
        ChatPostMessageResponse chatPostMessageResponse = new ChatPostMessageResponse();
        chatPostMessageResponse.setOk(false);
        return chatPostMessageResponse;
    }

}
