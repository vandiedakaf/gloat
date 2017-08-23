package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class StatsService {

    private final RestTemplate restTemplate;
    private final SlackUtilities slackUtilities;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatsService(RestTemplate restTemplate, SlackUtilities slackUtilities, UserCategoryRepository userCategoryRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.slackUtilities = slackUtilities;
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Async
    public void processRequest(Map<String, String> parameters, String userName) {

        final String teamId = parameters.get(SlackParameters.TEAM_ID.toString());
        final String channelId = parameters.get(SlackParameters.CHANNEL_ID.toString());

        Optional<User> slackUser = slackUtilities.getUser(teamId, userName);
        if (!slackUser.isPresent()) {
            Response response = new Response();
            response.setText("Sorry, seems like " + userName + " is some imaginary person.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);
        if (!category.isPresent()) {
            Response response = new Response();
            response.setText("No contests have been registered in this category.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        String userId = userName.isEmpty() ? parameters.get(SlackParameters.USER_ID.toString()) : slackUser.get().getId();
        Optional<com.vdda.jpa.User> user = userRepository.findByTeamIdAndUserId(teamId, userId);

        if (!user.isPresent()) {
            Response response = new Response();
            response.setText("No contests have been registered for this user.");
            restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return;
        }

        UserCategoryPK userCategoryPK = new UserCategoryPK(user.get(), category.get().getId());
        UserCategory userCategory = userCategoryRepository.findOne(userCategoryPK);

        Response response = new Response();
        response.setText("The Win:Loss:Draw values for this user is: " + userCategory.getWins() + ":" + userCategory.getLosses() + ":" + userCategory.getDraws());

        restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
    }

}
