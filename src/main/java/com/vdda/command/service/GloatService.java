package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Slf4j
public class GloatService {

    private final RestTemplate restTemplate;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public GloatService(RestTemplateBuilder restTemplateBuilder, UserCategoryRepository userCategoryRepository, CategoryRepository categoryRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Async
    public Future<?> processRequest(Map<String, String> parameters) {

        Response response = gloat(parameters);

        sendResponse(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);

        return new AsyncResult<>(null);
    }

    private Response gloat(Map<String, String> parameters) {

        Response response = new Response();

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        if (category == null) {
            response.setText("You can only gloat if you are ranked #1 in this category.");
            return response;
        }

        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.getId());

        if (userCategories.isEmpty()) {
            response.setText("You can only gloat if you are ranked #1 in this category.");
            return response;
        }

        if (!(userCategories.get(1).getUserCategoryPK().getUser().getUserId().equals(parameters.get(SlackParameters.USER_ID.toString())))) {
            response.setText("You can only gloat if you are ranked #1 in this category.");
            return response;
        }

        response.setText("GLOAT!");
        List<Attachment> attachments = new ArrayList<>();

        StringBuilder champDetails = new StringBuilder();
        champDetails.append("<@");
        champDetails.append(userCategories.get(1).getUserCategoryPK().getUser().getUserId());
        champDetails.append("> would like you all to bow before their greatness.");

        Attachment attachmentTitle = new Attachment();
        attachmentTitle.setFallback("GLOAT!");
        attachmentTitle.setTitle("The champ is in the house!");
        attachmentTitle.setText(champDetails.toString());
        attachmentTitle.setColor("#86C53C");
        attachments.add(attachmentTitle);

        response.setAttachments(attachments);
        return response;
    }

    private void sendResponse(String responseUrl, Response response) {
        restTemplate.postForLocation(responseUrl, response);
    }
}
