package com.vdda.command;

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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by francois
 * on 2017-01-04
 * for vandiedakaf solutions
 */
public class DrawServiceTest {

    private static final String USER_NAME = "@userName";
    private static final String USER_ID = "userId";
    private static final String TEAM_ID = "teamId";
    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEST_USER_ID = "testUserId";

    @Tested
    private DrawService drawService;

    @Mocked
    private RestTemplateBuilder restTemplateBuilder;
    @Mocked
    private SlackUtilities slackUtilities;
    @Mocked
    private RestTemplate restTemplate;

    private Map<String, String> parameters;
    private List<String> args;

    @Before
    public void setUp() throws Exception {
        drawService = new DrawService(restTemplateBuilder, slackUtilities);

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

        drawService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID));
        }};
    }

    private Optional<User> mockUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        return Optional.of(user);
    }
}