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

    private static final int TOP_COUNT = 3;

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

        if (userCategories.isEmpty()) {
            Response response = new Response();
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<Pair<Integer, UserCategory>> rankedUserCategories = getRankedPairs(userCategories);

        List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered = getFilteredList(parameters.get(SlackParameters.USER_ID.toString()), rankedUserCategories);

        Response response = new Response();
        response.setText("Top Contestants");

        List<Attachment> attachments = new ArrayList<>();

        Attachment attachmentTitle = new Attachment();
        attachmentTitle.setFallback("Top Contestants");
        attachmentTitle.setTitle("Rank. (Rating) Name [Wins-Losses-Draws]");
        attachmentTitle.setColor("#86C53C");
        attachments.add(attachmentTitle);

        StringBuilder textTopContestants = new StringBuilder();
        rankedUserCategoriesFiltered.stream()
                .filter(p -> p.getFirst() <= TOP_COUNT)
                .collect(toList())
                .forEach(u -> addContestant(u, textTopContestants));

        Attachment attachmentTopContestants = new Attachment();
        attachmentTopContestants.setText(textTopContestants.toString());
        attachmentTopContestants.setColor("#FFD700");
        attachments.add(attachmentTopContestants);

        if (rankedUserCategoriesFiltered.size() > TOP_COUNT) {
            StringBuilder textNeighbouringContestants = new StringBuilder();
            rankedUserCategoriesFiltered.stream()
                    .filter(p -> p.getFirst() > TOP_COUNT)
                    .collect(toList())
                    .forEach(u -> addContestant(u, textNeighbouringContestants));

            Attachment attachmentNeighbouringContestants = new Attachment();
            attachmentNeighbouringContestants.setText(textNeighbouringContestants.toString());
            attachmentNeighbouringContestants.setColor("#CD7F32");
            attachments.add(attachmentNeighbouringContestants);
        }

        response.setAttachments(attachments);
        return response;
    }

    private List<Pair<Integer, UserCategory>> getFilteredList(String userId, List<Pair<Integer, UserCategory>> rankedUserCategories) {

        List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered;

        Pair<Integer, UserCategory> userCategoryPair = rankedUserCategories
                .stream()
                .filter(u -> u.getSecond().getUserCategoryPK().getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (userCategoryPair != null) {
            rankedUserCategoriesFiltered = rankedUserCategories.stream()
                    .filter(p -> (p.getFirst() <= TOP_COUNT) || Math.abs(p.getFirst() - userCategoryPair.getFirst()) <= 1)
                    .collect(toList());
        } else {
            rankedUserCategoriesFiltered = rankedUserCategories.stream()
                    .filter(p -> p.getFirst() <= TOP_COUNT)
                    .collect(toList());
        }
        return rankedUserCategoriesFiltered;
    }

    private List<Pair<Integer, UserCategory>> getRankedPairs(List<UserCategory> userCategories) {
        return IntStream.range(0, userCategories.size())
                .mapToObj(i -> Pair.of(i + 1, userCategories.get(i)))
                .collect(toList());
    }

    private void addContestant(Pair<Integer, UserCategory> userCategoryPair, StringBuilder stringBuilder) {
        stringBuilder.append(userCategoryPair.getFirst());
        stringBuilder.append(". (");
        stringBuilder.append(userCategoryPair.getSecond().getElo());
        stringBuilder.append(") <@");
        stringBuilder.append(userCategoryPair.getSecond().getUserCategoryPK().getUser().getUserId());
        stringBuilder.append("> [");
        stringBuilder.append(userCategoryPair.getSecond().getWins());
        stringBuilder.append("-");
        stringBuilder.append(userCategoryPair.getSecond().getLosses());
        stringBuilder.append("-");
        stringBuilder.append(userCategoryPair.getSecond().getDraws());
        stringBuilder.append("]\n");
    }

    private void sendResponse(String responseUrl, Response response) {
        restTemplate.postForLocation(responseUrl, response);
    }
}
