package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.repository.UserUserCategoryRepository;
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

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class StatsServiceTest {
	private static final String RESPONSE_URL = "responseUrl";
	private static final String TEAM_ID = "teamId";
	private static final String USER_ID = "userId";
	private static final String OTHER_USER_ID = "otherUserId";
	private static final String CHANNEL_ID = "channelId";

	@Tested
	private StatsService statsService;

	@Mocked
	private UserCategoryRepository userCategoryRepository;
	@Mocked
	private UserRepository userRepository;
	@Mocked
	private CategoryRepository categoryRepository;
	@Mocked
	private UserUserCategoryRepository userUserCategoryRepository;
	@Mocked
	private RestTemplate restTemplate;
	@Mocked
	private SlackUtilities slackUtilities;

	private String baseRequestString;
	private Request request;

	@Before
	public void setUp() throws Exception {
		statsService = new StatsService(restTemplate, slackUtilities, userCategoryRepository, userUserCategoryRepository, categoryRepository, userRepository);

		baseRequestString = SlackParameters.USER_ID.toString() + "=" + USER_ID + "&" + SlackParameters.TEAM_ID.toString() + "=" + TEAM_ID + "&" + SlackParameters.RESPONSE_URL.toString() + "=" + RESPONSE_URL + "&" + SlackParameters.CHANNEL_ID.toString() + "=" + CHANNEL_ID + "&" + SlackParameters.TEXT.toString() + "=stats";
	}

	@Test
	public void processGolden() throws Exception {

		request = new Request(baseRequestString);

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategoryGolden();

			userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
			result = mockUserGolden();

			userCategoryRepository.findOne((UserCategoryPK) any);
			result = mockUserCategoryGolden();

			slackUtilities.getUserById(TEAM_ID, USER_ID);
			result = mockSlackUserGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getTitle(), containsString("Channel Stats"));
		}};
	}

	@Test
	public void processGoldenOtherUser() throws Exception {

		String requestString = baseRequestString + " " + OTHER_USER_ID;
		request = new Request(requestString);

		new Expectations() {{
			slackUtilities.getUserByUsername(TEAM_ID, OTHER_USER_ID);
			result = mockSlackUserGolden();

			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategoryGolden();

			userRepository.findByTeamIdAndUserId(TEAM_ID, OTHER_USER_ID);
			result = mockUserGolden();

			userCategoryRepository.findOne((UserCategoryPK) any);
			result = mockUserCategoryGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getTitle(), containsString("Channel Stats"));
		}};
	}

	@Test
	public void noOtherUser() throws Exception {

		String requestString = baseRequestString + " " + OTHER_USER_ID;
		request = new Request(requestString);

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("some imaginary person"));
		}};
	}

	@Test
	public void noCategory() throws Exception {
		request = new Request(baseRequestString);

		new Expectations() {{
			slackUtilities.getUserById(TEAM_ID, USER_ID);
			result = mockSlackUserGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("No contests have been registered in this category."));
		}};
	}

	@Test
	public void noUser() throws Exception {
		request = new Request(baseRequestString);

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategoryGolden();

			slackUtilities.getUserById(TEAM_ID, USER_ID);
			result = mockSlackUserGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("No contests have been registered for this user."));
		}};
	}

	@Test
	public void noUserCategory() throws Exception {
		request = new Request(baseRequestString);

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategoryGolden();

			userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
			result = mockUserGolden();

			userCategoryRepository.findOne((UserCategoryPK) any);
			result = null;

			slackUtilities.getUserById(TEAM_ID, USER_ID);
			result = mockSlackUserGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("No contests have been registered for this user."));
		}};
	}

	private Optional<Category> mockCategoryGolden() {
		return Optional.of(new Category(TEAM_ID, CHANNEL_ID));
	}

	private Optional<User> mockUserGolden() {
		return Optional.of(new User(TEAM_ID, CHANNEL_ID));
	}

	private Optional<com.github.seratch.jslack.api.model.User> mockSlackUserGolden() {
		com.github.seratch.jslack.api.model.User slackUser = new com.github.seratch.jslack.api.model.User();
		slackUser.setId(OTHER_USER_ID);
		return Optional.of(slackUser);
	}

	private UserCategory mockUserCategoryGolden() {
		User user = new User(TEAM_ID, USER_ID);
		UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(1);

		return userCategory;
	}
}
