package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class TenderServiceTest {

    private static final String RESPONSE_URL = "responseUrl";
    private static final String TEAM_ID = "teamId";
    private static final String USER_ID = "userId";
    private static final String CHANNEL_ID = "channelId";

    @Tested
    private TenderService tenderService;

    @Mocked
    private UserCategoryRepository userCategoryRepository;
    @Mocked
    private UserRepository userRepository;
    @Mocked
    private CategoryRepository categoryRepository;
    @Mocked
    private RestTemplate restTemplate;
    @Mocked
    private SlackUtilities slackUtilities;

    private Map<String, String> parameters;

    @Before
    public void setUp() throws Exception {
        tenderService = new TenderService(userCategoryRepository, userRepository, categoryRepository, slackUtilities);

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

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUserGolden();

            userCategoryRepository.findUserCategoryByUserCategoryPK((UserCategoryPK) any);
            result = mockUserCategoriesGolden();
        }};

        tenderService.processRequest(parameters);

        new Verifications() {{
            String message;
            slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, message = withCapture());
            assertThat(message, containsString(USER_ID));
        }};
    }

    @Test
    public void noCategory() throws Exception {
        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(anyString, anyString);
            result = null;
        }};

        tenderService.processRequest(parameters);

        new Verifications() {{
            String message;
            slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, message = withCapture());
            assertThat(message, containsString("(no rank)"));
        }};
    }

    @Test
    public void noUser() throws Exception {
        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategoryGolden();

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = null;
        }};

        tenderService.processRequest(parameters);

        new Verifications() {{
            String message;
            slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, message = withCapture());
            assertThat(message, containsString("(no rank)"));
        }};
    }

    @Test
    public void noUserCategory() throws Exception {
        new Expectations() {{
            categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
            result = mockCategoryGolden();

            userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
            result = mockUserGolden();

            userCategoryRepository.findUserCategoryByUserCategoryPK((UserCategoryPK) any);
            result = null;
        }};

        tenderService.processRequest(parameters);

        new Verifications() {{
            String message;
            slackUtilities.sendChatMessage(TEAM_ID, CHANNEL_ID, message = withCapture());
            assertThat(message, containsString("(no rank)"));
        }};
    }

    private Category mockCategoryGolden() {
        return new Category(TEAM_ID, CHANNEL_ID);
    }

    private User mockUserGolden() {
        return new User(TEAM_ID, CHANNEL_ID);
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
}
