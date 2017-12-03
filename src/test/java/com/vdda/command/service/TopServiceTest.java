package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Request;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TopServiceTest {

	private static final String RESPONSE_URL = "responseUrl";
	private static final String TEAM_ID = "teamId";
	private static final String CHANNEL_ID = "channelId";
	private static final String USER_ID = "userId";
	private static final String TEST_USER_ID_1 = "testUserId1";
	private static final String TEST_USER_ID_2 = "testUserId2";
	private static final String TEST_USER_ID_3 = "testUserId3";
	private static final String TEST_USER_ID_4 = "testUserId4";
	private static final String TEST_USER_ID_5 = "testUserId5";
	private static final String TEST_USER_ID_6 = "testUserId6";
	private static final String TEST_USER_ID_7 = "testUserId7";


	@Tested
	private TopService topService;

	@Mocked
	private UserCategoryRepository userCategoryRepository;
	@Mocked
	private CategoryRepository categoryRepository;
	@Mocked
	private RestTemplate restTemplate;

	private Request request;

	@Before
	public void setUp() throws Exception {
		topService = new TopService(restTemplate, userCategoryRepository, categoryRepository);

		request = new Request(SlackParameters.USER_ID.toString() + "=" + USER_ID + "&" + SlackParameters.TEAM_ID.toString() + "=" + TEAM_ID + "&" + SlackParameters.RESPONSE_URL.toString() + "=" + RESPONSE_URL + "&" + SlackParameters.TEXT.toString() + "=top " + USER_ID);
	}

	@Test
	public void processGolden() throws Exception {

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
			result = mockCategory();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesGolden();
		}};

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_1));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_2));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_3));
			assertThat(response.getAttachments().get(2).getText(), containsString(TEST_USER_ID_5));
			assertThat(response.getAttachments().get(2).getText(), containsString(USER_ID));
			assertThat(response.getAttachments().get(2).getText(), containsString(TEST_USER_ID_6));
		}};
	}

	@Test
	public void processGoldenUserInTop() throws Exception {
		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
			result = mockCategory();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesUserInTop();
		}};

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

			assertThat(response.getAttachments().get(0).getTitle(), containsString("Rank. (Rating) Name [Wins-Losses]"));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_1));
			assertThat(response.getAttachments().get(1).getText(), containsString(USER_ID));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_3));
			assertThat(response.getAttachments().size(), is(lessThan(3)));
		}};
	}

	@Test
	public void processGoldenUserDrawsIncluded() throws Exception {
		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
			result = mockCategory();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesDrawsIncluded();
		}};

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());

			assertThat(response.getAttachments().get(0).getTitle(), containsString("Rank. (Rating) Name [Wins-Losses-Draws]"));
		}};
	}

	@Test
	public void userNotFound() throws Exception {
		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
			result = mockCategory();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = mockUserCategoriesUserNotFound();
		}};

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_1));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_2));
			assertThat(response.getAttachments().get(1).getText(), containsString(TEST_USER_ID_3));
			assertThat(response.getAttachments().get(1).getText(), not(containsString(USER_ID)));
			assertThat(response.getAttachments().size(), is(lessThan(3)));
		}};

	}

	@Test
	public void noUsers() throws Exception {
		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
			result = mockCategory();

			userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(anyLong);
			result = new ArrayList<>();
		}};

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("No ranked"));
		}};

	}

	@Test
	public void noCategory() throws Exception {

		topService.processRequest(request);

		new Verifications() {{
			Response response;
			restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
			assertThat(response.getText(), containsString("No contests"));
		}};

	}

	private Optional<Category> mockCategory() {
		Category category = new Category(TEAM_ID, CHANNEL_ID);
		category.setId(1L);
		return Optional.of(category);
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

	private List<UserCategory> mockUserCategoriesUserInTop() {
		List<UserCategory> userCategories = new ArrayList<>();

		User user = new User(TEAM_ID, TEST_USER_ID_1);
		UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(5);
		userCategory.setWins(4);
		userCategory.setLosses(6);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, USER_ID);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(1);
		userCategory.setWins(4);
		userCategory.setLosses(6);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_3);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(3);
		userCategory.setWins(4);
		userCategory.setLosses(6);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_4);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(2);
		userCategory.setWins(4);
		userCategory.setLosses(6);
		userCategories.add(userCategory);

		return userCategories;
	}

	private List<UserCategory> mockUserCategoriesUserNotFound() {
		List<UserCategory> userCategories = new ArrayList<>();

		User user = new User(TEAM_ID, TEST_USER_ID_1);
		UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
		UserCategory userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(5);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_2);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(4);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_3);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(3);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		user = new User(TEAM_ID, TEST_USER_ID_4);
		userCategoryPK = new UserCategoryPK(user, 1L);
		userCategory = new UserCategory(userCategoryPK);
		userCategory.setElo(2);
		userCategory.setWins(4);
		userCategory.setLosses(4);
		userCategory.setDraws(4);
		userCategories.add(userCategory);

		return userCategories;
	}

	private List<UserCategory> mockUserCategoriesDrawsIncluded() {
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
		userCategory.setDraws(10);
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
		userCategory.setDraws(10);
		userCategories.add(userCategory);

		return userCategories;
	}
}
