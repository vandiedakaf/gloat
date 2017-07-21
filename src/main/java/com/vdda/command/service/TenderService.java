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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Future;

@Service
@Slf4j
public class TenderService {

    private final RestTemplate restTemplate;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SlackUtilities slackUtilities;

    @Autowired
    public TenderService(RestTemplateBuilder restTemplateBuilder, UserCategoryRepository userCategoryRepository, UserRepository userRepository, CategoryRepository categoryRepository, SlackUtilities slackUtilities) {
        this.restTemplate = restTemplateBuilder.build();
        this.userCategoryRepository = userCategoryRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.slackUtilities = slackUtilities;
    }

    @Async
    public Future<?> processRequest(Map<String, String> parameters) {

        tender(parameters);

        return new AsyncResult<>(null);
    }

    private void tender(Map<String, String> parameters) {

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        StringBuilder tenderDetails = new StringBuilder();
        tenderDetails.append("<@");
        tenderDetails.append(parameters.get(SlackParameters.USER_ID.toString()));
        tenderDetails.append(">");

        if (category == null) {
            noRank(parameters, tenderDetails);
            return;
        }

        User user = userRepository.findByTeamIdAndUserId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.USER_ID.toString()));

        if (user == null) {
            noRank(parameters, tenderDetails);
            return;
        }

        UserCategoryPK userCategoryPK = new UserCategoryPK(user, category.getId());
        UserCategory userCategory = userCategoryRepository.findUserCategoryByUserCategoryPK(userCategoryPK);

        if (userCategory == null) {
            noRank(parameters, tenderDetails);
            return;
        }

        tenderDetails.append(" (");
        tenderDetails.append(userCategory.getElo());
        tenderDetails.append(") is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()), tenderDetails.toString());
    }

    private void noRank(Map<String, String> parameters, StringBuilder tenderDetails) {
        tenderDetails.append(" (no rank) is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()), tenderDetails.toString());
    }
}
