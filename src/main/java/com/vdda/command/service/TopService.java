package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.runtime.powerassert.SourceText;
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
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

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
    public Future<?> processRequest(Map<String, String> parameters) {

        Response response = getTopUsers(parameters);

        restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);

        return new AsyncResult<>(null);
    }

    private Response getTopUsers(Map<String, String> parameters) {

        Response response = new Response();

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        if (category == null) {
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.getId());

        if (userCategories.isEmpty()) {
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<Pair<Integer, UserCategory>> rankedUserCategories = determineRankedPairs(userCategories);

        List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered = getFilteredList(parameters.get(SlackParameters.USER_ID.toString()), rankedUserCategories);

        response.setText("Top Contestants");

        int drawSum = rankedUserCategoriesFiltered.stream()
                .mapToInt(u -> u.getSecond().getDraws())
                .sum();
        boolean containsDraws = drawSum > 0;

        List<Attachment> attachments = new ArrayList<>();

        Attachment attachmentTitle = new Attachment();
        attachmentTitle.setFallback("Top Contestants");
        if (containsDraws) {
            attachmentTitle.setTitle("Rank. (Rating) Name [Wins-Losses-Draws]");
        } else {
            attachmentTitle.setTitle("Rank. (Rating) Name [Wins-Losses]");
        }
        attachmentTitle.setColor("#86C53C");
        attachments.add(attachmentTitle);

        StringBuilder textTopContestants = new StringBuilder();
        rankedUserCategoriesFiltered.stream()
                .filter(p -> p.getFirst() <= TOP_COUNT)
                .collect(toList())
                .forEach(u -> addContestant(u, textTopContestants, containsDraws));

        Attachment attachmentTopContestants = new Attachment();
        attachmentTopContestants.setText(textTopContestants.toString());
        attachmentTopContestants.setColor("#FFD700");
        attachments.add(attachmentTopContestants);

        if (rankedUserCategoriesFiltered.size() > TOP_COUNT) {
            StringBuilder textNeighbouringContestants = new StringBuilder();
            rankedUserCategoriesFiltered.stream()
                    .filter(p -> p.getFirst() > TOP_COUNT)
                    .collect(toList())
                    .forEach(u -> addContestant(u, textNeighbouringContestants, containsDraws));

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
                .filter(p -> p.getSecond().getUserCategoryPK().getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        Predicate<Pair<Integer, UserCategory>> topFilterPredicate = topPlayersOnly();
        if (userCategoryPair != null) {
            topFilterPredicate = topPlayersAndNeighbours(userCategoryPair.getFirst());
        }

        rankedUserCategoriesFiltered = rankedUserCategories.stream()
                .filter(topFilterPredicate)
                .collect(toList());

        return rankedUserCategoriesFiltered;
    }

    private Predicate<Pair<Integer, UserCategory>> topPlayersOnly() {
        return p -> p.getFirst() <= TOP_COUNT;
    }

    private Predicate<Pair<Integer, UserCategory>> topPlayersAndNeighbours(Integer userRank) {
        return p -> (p.getFirst() <= TOP_COUNT) || Math.abs(p.getFirst() - userRank) <= 1;
    }

    private List<Pair<Integer, UserCategory>> determineRankedPairs(List<UserCategory> userCategories) {
        return IntStream.range(0, userCategories.size())
                .mapToObj(i -> Pair.of(i + 1, userCategories.get(i)))
                .collect(toList());
    }

    private void addContestant(Pair<Integer, UserCategory> userCategoryPair, StringBuilder stringBuilder, boolean containsDraws) {
        stringBuilder.append(userCategoryPair.getFirst());
        stringBuilder.append(". (");
        stringBuilder.append(userCategoryPair.getSecond().getElo());
        stringBuilder.append(") <@");
        stringBuilder.append(userCategoryPair.getSecond().getUserCategoryPK().getUser().getUserId());
        stringBuilder.append("> [");
        stringBuilder.append(userCategoryPair.getSecond().getWins());
        stringBuilder.append("-");
        stringBuilder.append(userCategoryPair.getSecond().getLosses());
        if (containsDraws) {
            stringBuilder.append("-");
            stringBuilder.append(userCategoryPair.getSecond().getDraws());
        }
        stringBuilder.append("]\n");
    }
}
