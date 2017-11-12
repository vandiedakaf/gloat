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
import static org.hamcrest.Matchers.containsString;

public class SeriesServiceTest {

    private static final String USER_NAME = "@userName";
    private static final String USER_ID = "userId";
    private static final String TEAM_ID = "teamId";
    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEST_USER_ID = "testUserId";
    private static final String OUTCOME = "wld";
	private static final String OUTCOME_MULTIPLE = "wWlLdD";

    @Tested
    private SeriesService seriesService;

    @Mocked
    private SlackUtilities slackUtilities;
    @Mocked
    private RestTemplate restTemplate;

    private Map<String, String> parameters;
    private List<String> args;

    @Before
    public void setUp() throws Exception {
        seriesService = new SeriesService(restTemplate, slackUtilities);

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

		args.add(OUTCOME);
        seriesService.processRequest(parameters, args);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID));
			assertThat(response.getAttachments().get(0).getText(), containsString("1 win, 1 loss, 1 draw"));
        }};
    }

	@Test
	public void processGoldenMultipleContests() throws Exception {

		new Expectations() {{
			slackUtilities.getUser(TEAM_ID, USER_NAME);
			result = mockUser();
		}};

		args.add(OUTCOME_MULTIPLE);
		seriesService.processRequest(parameters, args);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID));
			assertThat(response.getAttachments().get(0).getText(), containsString("2 wins, 2 losses, 2 draws"));
		}};
	}

    private Optional<User> mockUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        return Optional.of(user);
    }
}
