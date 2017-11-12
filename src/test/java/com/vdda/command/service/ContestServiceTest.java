package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ContestServiceTest {

    private static final String CALLBACK_ID = "callbackId";
    private static final String USER_NAME = "@userName";
    private static final String USER_NAME_SLACKBOT_1 = "@slackbot";
    private static final String USER_NAME_SLACKBOT_2 = "slackbot";
    private static final String USER_ID = "userId";
    private static final String TEAM_ID = "teamId";
    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEST_USER_ID = "testUserId";

    @Tested
    private ContestService contestService;

    @Mocked
    private SlackUtilities slackUtilities;
    @Mocked
    private RestTemplate restTemplate;

    private Map<String, String> parameters;
    private List<String> args;

    @Before
    public void setUp() throws Exception {
        contestService = new ContestService(restTemplate, slackUtilities) {
            @Override
            protected Response confirmationButton(User user) {
                Response response = new Response();
                response.setText("test");
                return response;
            }
        };

        parameters = new HashMap<>();
        parameters.put(SlackParameters.TEAM_ID.toString(), TEAM_ID);
        parameters.put(SlackParameters.RESPONSE_URL.toString(), RESPONSE_URL);
        parameters.put(SlackParameters.USER_ID.toString(), USER_ID);

        args = new ArrayList<>();
        args.add(USER_NAME);
    }

    @Test
    public void processGolden() throws Exception {

        new Expectations() {{
            slackUtilities.getUser(TEAM_ID, USER_NAME);
            result = mockUser();
        }};

        contestService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("test"));
        }};
    }

    @Test
    public void processNoUser() throws Exception {

        contestService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

            assertThat(response.getText(), containsString("imaginary person"));
        }};
    }

    @Test
    public void processSelf() throws Exception {

        new Expectations() {{
            slackUtilities.getUser(TEAM_ID, USER_NAME);
            result = mockUserSelf();
        }};

        args = new ArrayList<>();
        args.add(USER_NAME);
        contestService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

            assertThat(response.getText(), containsString("can't compete against yourself or slackbot"));
        }};
    }

    @Test
    public void processSlackbot1() throws Exception {

        new Expectations() {{
            slackUtilities.getUser(TEAM_ID, USER_NAME_SLACKBOT_1);
            result = mockUser();
        }};

        args = new ArrayList<>();
        args.add(USER_NAME_SLACKBOT_1);
        contestService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

            assertThat(response.getText(), containsString("can't compete against yourself or slackbot"));
        }};
    }

    @Test
    public void processSlackbot2() throws Exception {

        new Expectations() {{
            slackUtilities.getUser(TEAM_ID, USER_NAME_SLACKBOT_2);
            result = mockUser();
        }};

        args = new ArrayList<>();
        args.add(USER_NAME_SLACKBOT_2);
        contestService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

            assertThat(response.getText(), containsString("can't compete against yourself or slackbot"));
        }};
    }

    private Optional<User> mockUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        return Optional.of(user);
    }

    private Optional<User> mockUserSelf() {
        User user = new User();
        user.setId(USER_ID);
        return Optional.of(user);
    }
}
