package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GloatService {

    private final RestTemplate restTemplate;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SlackUtilities slackUtilities;

    @Autowired
    public GloatService(RestTemplate restTemplate, UserCategoryRepository userCategoryRepository, CategoryRepository categoryRepository, SlackUtilities slackUtilities) {
        this.restTemplate = restTemplate;
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.slackUtilities = slackUtilities;
    }

    @Async
    public void processRequest(Map<String, String> parameters) {

        gloat(parameters);
    }

    private void gloat(Map<String, String> parameters) {

        Response response = new Response();

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        if (category == null) {
            response.setText("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.getId(), 10); // TODO replace with global constant

        if (userCategories.isEmpty()) {
            response.setText("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        String topUserId = userCategories.get(0).getUserCategoryPK().getUser().getUserId();

        if (!(topUserId.equals(parameters.get(SlackParameters.USER_ID.toString())))) {
            response.setText("You can only gloat if you are ranked #1 in this category. Currently this rank belongs to <@" + topUserId + ">.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        StringBuilder champDetails = new StringBuilder();
        champDetails.append(":trophy: <@");
        champDetails.append(topUserId);
        champDetails.append("> would like you all to bow before their greatness :trophy:");
        slackUtilities.sendChatMessage(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()), champDetails.toString());
    }
}
