package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

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
    public void processRequest(Request request) {

        gloat(request);
    }

    private void gloat(Request request) {

        Response response = new Response();

        Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.CHANNEL_ID.toString()));

        if (!category.isPresent()) {
            response.setText("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category.");
            restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        // TODO remove calibration game constraint and use streaming (e.g. filter and first) to get top user.
        // TODO replace literal "10" with global config
        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.get().getId(), 10);

        if (userCategories.isEmpty()) {
            response.setText("You can only gloat if you are ranked #1 in this category. No contests have been registered in this category.");
            restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        String topUserId = userCategories.get(0).getUserCategoryPK().getUser().getUserId();

        if (!(topUserId.equals(request.getParameter(SlackParameters.USER_ID.toString())))) {
            response.setText("You can only gloat if you are ranked #1 in this category. Currently this rank belongs to <@" + topUserId + ">.");
            restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        String champDetails = ":trophy: <@" +
                topUserId +
                "> would like you all to bow before their greatness :trophy:";
        slackUtilities.sendChatMessage(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.CHANNEL_ID.toString()), champDetails);
    }
}
