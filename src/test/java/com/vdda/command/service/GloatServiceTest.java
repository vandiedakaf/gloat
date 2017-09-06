package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
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

public class GloatServiceTest {

    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";
    private static final String CHANNEL_ID = "channelId";
    private static final String TEST_USER_ID_1 = "testUserId1";

    @Tested
    private GloatService gloatService;

    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private CategoryRepository categoryRepository;
    @Mocked
    private RestTemplate restTemplate;
    @Mocked
    private SlackUtilities slackUtilities;

    private Map<String, String> parameters;

    @Before
    public void setUp() throws Exception {
        gloatService = new GloatService(restTemplate, userCategoryRepository, categoryRepository, slackUtilities);

        parameters = new HashMap<>();
        parameters.put(SlackParameters.TEAM_ID.toString(), TEAM_ID);
        parameters.put(SlackParameters.RESPONSE_URL.toString(), RESPONSE_URL);
        parameters.put(SlackParameters.USER_ID.toString(), USER_ID);
        parameters.put(SlackParameters.CHANNEL_ID.toString(), CHANNEL_ID);
    }

    @Test
    public void processGolden() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategoryGolden();

            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong, anyInt);
            result = mockUserCategoriesGolden();
        }};

        gloatService.processRequest(parameters);

        new Verifications() {{
            String message;
            slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, message = withCapture());
            assertThat(message, containsString(USER_ID));
        }};
    }

    @Test
    public void noUsers() throws Exception {
        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategoryGolden();

            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong, anyInt);
            result = new ArrayList<>();
        }};

        gloatService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category."));
        }};

    }

    @Test
    public void noCategory() throws Exception {

        gloatService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category."));
        }};
    }

    @Test
    public void notNumberOne() throws Exception {

        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategoryGolden();

            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong, anyInt);
            result = mockUserCategoriesNotNumberOne();
        }};

        gloatService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("You can only gloat if you are ranked #1 in this category."));
        }};
    }

    private Optional<Category> mockCategoryGolden() {
        return Optional.of(new Category(TEAM_ID, CHANNEL_ID));
    }

    private List<UserCategory> mockUserCategoriesGolden() {
        List<UserCategory> userCategories = new ArrayList<>();

        User user = new User(TEAM_ID, USER_ID);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK);
        userCategory.setElo(1);
        userCategories.add(userCategory);

        return userCategories;
    }

    private List<UserCategory> mockUserCategoriesNotNumberOne() {
        List<UserCategory> userCategories = new ArrayList<>();

        User user = new User(TEAM_ID, TEST_USER_ID_1);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK);
        userCategory.setElo(1);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, USER_ID);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK);
        userCategory.setElo(3);
        userCategories.add(userCategory);

        return userCategories;
    }
}
