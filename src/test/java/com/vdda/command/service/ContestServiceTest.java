package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

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

	private String baseRequestString;
	private Request request;

	@Before
	public void setUp() throws Exception {
		contestService = new ContestService(restTemplate, slackUtilities) {
			@Override
			protected Response confirmationButton(User user, List<String> arguments) {
				Response response = new Response();
				response.setText("test");
				return response;
			}
		};

		baseRequestString = SlackParameters.USER_ID.toString() + "=" + USER_ID + "&" + SlackParameters.TEAM_ID.toString() + "=" + TEAM_ID + "&" + SlackParameters.RESPONSE_URL.toString() + "=" + RESPONSE_URL;
	}

	@Test
	public void processGolden() throws Exception {

		String requestString = baseRequestString + "&" + SlackParameters.TEXT.toString() + "=" + USER_NAME;
		request = new Request(requestString);

		new Expectations() {{
			slackUtilities.getUserByUsername(TEAM_ID, USER_NAME);
			result = mockUser();
		}};

		contestService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("test"));
		}};
	}

	@Test
	public void processNoUser() throws Exception {

		String requestString = baseRequestString + "&" + SlackParameters.TEXT.toString() + "=" + USER_NAME;
		request = new Request(requestString);

		contestService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

			assertThat(response.getText(), containsString("imaginary person"));
		}};
	}

	@Test
	public void processSelf() throws Exception {

		String requestString = baseRequestString + "&" + SlackParameters.TEXT.toString() + "=" + USER_NAME;
		request = new Request(requestString);

		new Expectations() {{
			slackUtilities.getUserByUsername(TEAM_ID, USER_NAME);
			result = mockUserSelf();
		}};

		contestService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

			assertThat(response.getText(), containsString("can't compete against yourself or slackbot"));
		}};
	}

	@Test
	public void processSlackbot1() throws Exception {

		String requestString = baseRequestString + "&" + SlackParameters.TEXT.toString() + "=" + USER_NAME_SLACKBOT_1;
		request = new Request(requestString);

		new Expectations() {{
			slackUtilities.getUserByUsername(TEAM_ID, USER_NAME_SLACKBOT_1);
			result = mockUser();
		}};

		contestService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

			assertThat(response.getText(), containsString("can't compete against yourself or slackbot"));
		}};
	}

	@Test
	public void processSlackbot2() throws Exception {

		String requestString = baseRequestString + "&" + SlackParameters.TEXT.toString() + "=" + USER_NAME_SLACKBOT_2;
		request = new Request(requestString);

		new Expectations() {{
			slackUtilities.getUserByUsername(TEAM_ID, USER_NAME_SLACKBOT_2);
			result = mockUser();
		}};
		contestService.processRequest(request);

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
