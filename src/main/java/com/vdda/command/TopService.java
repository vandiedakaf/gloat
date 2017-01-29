package com.vdda.command;

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
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by francois
 * on 2016-12-30
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class TopService {

    private final static int TOP_COUNT = 3;

    private final RestTemplate restTemplate;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TopService(RestTemplateBuilder restTemplateBuilder, UserCategoryRepository userCategoryRepository, CategoryRepository categoryRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Async
    Future<?> processRequest(Map<String, String> parameters) {

        Response response = getTopUsers(parameters);

        sendResponse(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);

        return new AsyncResult<>(null);
    }

    private Response getTopUsers(Map<String, String> parameters) {

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        if (category == null) {
            Response response = new Response();
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.getId());

        if (userCategories == null) {
            Response response = new Response();
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<Pair<Integer, UserCategory>> rankedUserCategories = getRankedPairs(userCategories);

        List<Pair<Integer, UserCategory>> rankedUserCategoriesTop = rankedUserCategories.subList(0, Math.min(TOP_COUNT, rankedUserCategories.size()));

        addRequestingUser(parameters.get(SlackParameters.USER_ID.toString()), rankedUserCategories, rankedUserCategoriesTop);

        StringBuilder stringBuilder = new StringBuilder();
        rankedUserCategoriesTop.forEach(u -> addContestant(u, stringBuilder));

        Response response = new Response();
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();

        attachment.setFallback("Top Contestants");
        attachment.setTitle("Top Contestants (Rank, Name & Wins-Losses)");
        attachment.setText(stringBuilder.toString());
        attachment.setColor("#86C53C");
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }

    private List<Pair<Integer, UserCategory>> getRankedPairs(List<UserCategory> userCategories) {
        return IntStream.range(0, userCategories.size())
                .parallel()
                .mapToObj(i -> Pair.of(i + 1, userCategories.get(i)))
                .collect(toList());
    }

    private void addRequestingUser(String userId, List<Pair<Integer, UserCategory>> userCategories, List<Pair<Integer, UserCategory>> userCategoriesTop) {
        boolean userInTopList = userCategoriesTop
                .stream()
                .anyMatch(u -> u.getSecond().getUserCategoryPK().getUser().getUserId().equals(userId));

        if (!userInTopList) {
            Pair<Integer, UserCategory> userCategoryPair = userCategories
                    .stream()
                    .filter(u -> u.getSecond().getUserCategoryPK().getUser().getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (userCategoryPair != null) {
                userCategoriesTop.add(userCategoryPair);
            }
        }
    }

    private void addContestant(Pair<Integer, UserCategory> userCategoryPair, StringBuilder stringBuilder) {
        stringBuilder.append(userCategoryPair.getFirst());
        stringBuilder.append(". (");
        stringBuilder.append(userCategoryPair.getSecond().getElo());
        stringBuilder.append(") <@");
        stringBuilder.append(userCategoryPair.getSecond().getUserCategoryPK().getUser().getUserId());
        stringBuilder.append("> [");
        stringBuilder.append(userCategoryPair.getSecond().getContestWins());
        stringBuilder.append("-");
        stringBuilder.append(userCategoryPair.getSecond().getContestTotal() - userCategoryPair.getSecond().getContestWins());
        stringBuilder.append("]\n");
    }

    private void sendResponse(String responseUrl, Response response) {
        restTemplate.postForLocation(responseUrl, response);
    }
}
