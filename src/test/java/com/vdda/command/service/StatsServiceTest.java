package com.vdda.command.service;

import com.vdda.domain.jpa.*;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserRepository;
import com.vdda.domain.repository.UserUserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatsServiceTest {
	private static final String RESPONSE_URL = "responseUrl";
	private static final String TEAM_ID = "teamId";
	private static final String USER_ID = "userId";
	private static final String OTHER_USER_ID = "otherUserId";
	private static final String CHANNEL_ID = "channelId";
	private static final String TEST_USER_ID_1 = "testUserId1";
	private static final String TEST_USER_ID_2 = "testUserId2";
	private static final String TEST_USER_ID_3 = "testUserId3";
	private static final String TEST_USER_ID_4 = "testUserId4";
	private static final String TEST_USER_ID_5 = "testUserId5";
	private static final String TEST_USER_ID_6 = "testUserId6";
	private static final String TEST_USER_ID_7 = "testUserId7";

	@Autowired
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

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesDrawsExcluded();

			userUserCategoryRepository.findWilsonMax(anyLong, anyLong);
			result = mockFrenemyNemesis();

			userUserCategoryRepository.findWilsonMin(anyLong, anyLong);
			result = mockFrenemyNemesis();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getTitle(), containsString("Channel Stats"));

			assertThat(response.getAttachments().get(1).getFields().get(2).getTitle(), containsString("Frenemy"));
			assertThat(response.getAttachments().get(1).getFields().get(3).getTitle(), containsString("Nemesis"));
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

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesDrawsExcluded();
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
	public void noSlackUser() throws Exception {
		request = new Request(baseRequestString);

		new Expectations() {{
			slackUtilities.getUserById(TEAM_ID, USER_ID);
			result = Optional.empty();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("Strange, it seems like you don't exist on slack."));
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

	@Test
	public void processGoldenNoMaxStreak() throws Exception {

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

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesDrawsExcluded();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByStreakCountDescStreakTypeDescModifiedDesc(anyLong);
			result = mockMaxStreak();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getFields().get(1).getTitle(), containsString("Longest Current Streak"));
			assertThat(response.getAttachments().get(0).getFields().get(2).getTitle(), containsString("Rank. (Rating) Name [Wins-Losses]"));
		}};
	}

	@Test
	public void processGoldenUserNotInTop() throws Exception {

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

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesGolden();
		}};

		statsService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(0).getFields().get(1).getTitle(), containsString("Rank. (Rating) Name [Wins-Losses-Draws]"));
			assertThat(response.getAttachments().get(0).getFields().get(1).getValue(), containsString(TEST_USER_ID_1));
			assertThat(response.getAttachments().get(0).getFields().get(1).getValue(), containsString(TEST_USER_ID_2));
			assertThat(response.getAttachments().get(0).getFields().get(1).getValue(), containsString(TEST_USER_ID_3));
			assertThat(response.getAttachments().get(0).getFields().get(2).getValue(), containsString(TEST_USER_ID_5));
			assertThat(response.getAttachments().get(0).getFields().get(2).getValue(), containsString(USER_ID));
			assertThat(response.getAttachments().get(0).getFields().get(2).getValue(), containsString(TEST_USER_ID_6));
		}};
	}

	private Optional<Category> mockCategoryGolden() {
		return Optional.of(new Category(TEAM_ID, CHANNEL_ID));
	}

	private Optional<User> mockUserGolden() {
		return Optional.of(new User(TEAM_ID, CHANNEL_ID));
	}

	private List<UserCategory> mockMaxStreak() {
		UserCategoryPK userCategoryPK = new UserCategoryPK(new User("teamId", "userId"), null);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setWins(10);
		userCategory.setLosses(10);
		userCategory.setStreakType(ContestOutcome.WIN);
		return Arrays.asList(userCategory);
	}

	private List<UserUserCategory> mockFrenemyNemesis() {
		UserUserCategoryPK userUserCategoryPK = new UserUserCategoryPK(null, new User("teamId", "userId"), null);
		UserUserCategory userUserCategory = new UserUserCategory(userUserCategoryPK);
		userUserCategory.setWins(10);
		userUserCategory.setLosses(10);
		return Arrays.asList(userUserCategory);
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

	private List<UserCategory> mockUserCategoriesDrawsExcluded() {
		List<UserCategory> userCategories = new ArrayList<>();

		User user = new User(TEAM_ID, TEST_USER_ID_1);
		UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(5);
		userCategory.setWins(10);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, USER_ID);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(1);
		userCategory.setWins(10);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_3);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(3);
		userCategory.setLosses(10);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_4);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(2);
		userCategory.setLosses(10);
		userCategories.add(userCategory);

		return userCategories;
	}

	private List<UserCategory> mockUserCategoriesGolden() {
		List<UserCategory> userCategories = new ArrayList<>();

		User user = new User(TEAM_ID, TEST_USER_ID_1);
		UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(8);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_2);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(7);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_3);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(6);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_4);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(5);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_5);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(4);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, USER_ID);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(3);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_6);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(2);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_7);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(1);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		return userCategories;
	}
}
