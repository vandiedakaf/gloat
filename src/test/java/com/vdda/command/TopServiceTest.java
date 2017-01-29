package com.vdda.command;

import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by francois
 * on 2017-01-19
 * for vandiedakaf solutions
 */
public class TopServiceTest {

    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";
    private static final String TEST_USER_ID_1 = "testUserId1";
    private static final String TEST_USER_ID_2 = "testUserId2";
    private static final String TEST_USER_ID_3 = "testUserId3";
    private static final String TEST_USER_ID_4 = "testUserId4";


    @Tested
    private TopService topService;

    @Mocked
    private RestTemplateBuilder restTemplateBuilder;
    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private CategoryRepository categoryRepository;
    @Mocked
    private RestTemplate restTemplate;

    private Map<String, String> parameters;

    @Before
    public void setUp() throws Exception {
        topService = new TopService(restTemplateBuilder, userCategoryRepository, categoryRepository);

        parameters = new HashMap<>();
        parameters.put(SlackParameters.TEAM_ID.toString(), TEAM_ID);
        parameters.put(SlackParameters.RESPONSE_URL.toString(), RESPONSE_URL);
        parameters.put(SlackParameters.USER_ID.toString(), USER_ID);
    }

    @Test
    public void processGolden() throws Exception {

        new Expectations() {{
            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong);
            result = mockUserCategoriesGolden();
        }};

        topService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_1));
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_2));
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_3));
            assertThat(response.getAttachments().get(0).getText(), containsString(USER_ID));
            assertThat(response.getAttachments().get(0).getText(), not(containsString(TEST_USER_ID_4)));
        }};
    }

    @Test
    public void userInTop() throws Exception {
        new Expectations() {{
            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong);
            result = mockUserCategoriesUserInTop();
        }};

        topService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_1));
            assertThat(response.getAttachments().get(0).getText(), containsString(USER_ID));
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_3));
            assertThat(response.getAttachments().get(0).getText(), not(containsString(TEST_USER_ID_4)));
        }};
    }

    @Test
    public void userNotFound() throws Exception {
        new Expectations() {{
            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong);
            result = mockUserCategoriesUserNotFound();
        }};

        topService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_1));
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_2));
            assertThat(response.getAttachments().get(0).getText(), containsString(TEST_USER_ID_3));
            assertThat(response.getAttachments().get(0).getText(), not(containsString(USER_ID)));
        }};

    }

    @Test
    public void noUsers() throws Exception {
        new Expectations() {{
            userCategoryRepository.findAllByCategoryIdOrderByEloDesc(anyLong);
            result = null;
        }};

        topService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("No contests"));
        }};

    }

    @Test
    public void noCategory() throws Exception {
        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
            result = null;
        }};

        topService.processRequest(parameters);

        new Verifications() {{
            Response response;
            restTemplate.postForLocation(RESPONSE_URL, response = withCapture());
            assertThat(response.getText(), containsString("No contests"));
        }};

    }

    private List<UserCategory> mockUserCategoriesGolden() {
        List<UserCategory> userCategories = new ArrayList<>();

        User user = new User(TEAM_ID, TEST_USER_ID_1);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 5);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_2);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 4);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_3);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 3);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_4);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 2);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, USER_ID);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 1);
        userCategories.add(userCategory);

        return userCategories;
    }

    private List<UserCategory> mockUserCategoriesUserInTop() {
        List<UserCategory> userCategories = new ArrayList<>();

        User user = new User(TEAM_ID, TEST_USER_ID_1);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 5);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, USER_ID);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 1);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_3);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 3);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_4);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 2);
        userCategories.add(userCategory);

        return userCategories;
    }

    private List<UserCategory> mockUserCategoriesUserNotFound() {
        List<UserCategory> userCategories = new ArrayList<>();

        User user = new User(TEAM_ID, TEST_USER_ID_1);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 1L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 5);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_2);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 4);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_3);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 3);
        userCategories.add(userCategory);

        user = new User(TEAM_ID, TEST_USER_ID_4);
        userCategoryPK = new UserCategoryPK(user, 1L);
        userCategory = new UserCategory(userCategoryPK, 2);
        userCategories.add(userCategory);

        return userCategories;
    }
}
