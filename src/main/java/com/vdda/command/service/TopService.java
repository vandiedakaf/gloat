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
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TopService {

    private static final int NUM_TOP_USERS = 3;

    private final RestTemplate restTemplate;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;

    private static final String COLOUR_GREEN = "#86C53C";
    private static final String COLOUR_GOLD = "#FFD700";
    private static final String COLOUR_BRONZE = "#CD7F32";

    @Autowired
    public TopService(RestTemplateBuilder restTemplateBuilder, UserCategoryRepository userCategoryRepository, CategoryRepository categoryRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Async
    public Future<?> processRequest(Map<String, String> parameters) {

        return processRequest(parameters, NUM_TOP_USERS);
    }

    @Async
    public Future<?> processRequest(Map<String, String> parameters, int numTopUsers) {

        Response response = getTopUsers(parameters, numTopUsers);

        restTemplate.postForLocation(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);

        return new AsyncResult<>(null);
    }

    private Response getTopUsers(Map<String, String> parameters, int numTopUsers) {

        Response response = new Response();

        Category category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        if (category == null) {
            response.setText("No contests have been registered in this category.");
            return response;
        }

        List<UserCategory> userCategories = userCategoryRepository.findAllByCategoryIdOrderByEloDesc(category.getId(), 10); // TODO replace with global constant

        if (userCategories.isEmpty()) {
            response.setText("No ranked players have been found in this category. Players might still be in calibration phase."); // TODO add calibration phase requirement
            return response;
        }

        List<Pair<Integer, UserCategory>> rankedUserCategories = determineRankedPairs(userCategories);

        List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered = getFilteredList(parameters.get(SlackParameters.USER_ID.toString()), rankedUserCategories, numTopUsers);

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
        attachmentTitle.setColor(COLOUR_GREEN);
        attachments.add(attachmentTitle);

        StringBuilder textTopContestants = new StringBuilder();
        rankedUserCategoriesFiltered.stream()
                .filter(p -> p.getFirst() <= numTopUsers)
                .collect(toList())
                .forEach(u -> addContestant(u, textTopContestants, containsDraws));

        Attachment attachmentTopContestants = new Attachment();
        attachmentTopContestants.setText(textTopContestants.toString());
        attachmentTopContestants.setColor(COLOUR_GOLD);
        attachments.add(attachmentTopContestants);

        if (rankedUserCategoriesFiltered.size() > numTopUsers) {
            StringBuilder textNeighbouringContestants = new StringBuilder();
            rankedUserCategoriesFiltered.stream()
                    .filter(p -> p.getFirst() > numTopUsers)
                    .collect(toList())
                    .forEach(u -> addContestant(u, textNeighbouringContestants, containsDraws));

            Attachment attachmentNeighbouringContestants = new Attachment();
            attachmentNeighbouringContestants.setText(textNeighbouringContestants.toString());
            attachmentNeighbouringContestants.setColor(COLOUR_BRONZE);
            attachments.add(attachmentNeighbouringContestants);
        }

        response.setAttachments(attachments);
        return response;
    }

    private List<Pair<Integer, UserCategory>> getFilteredList(String userId, List<Pair<Integer, UserCategory>> rankedUserCategories, int numTopUsers) {

        List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered;

        Pair<Integer, UserCategory> userCategoryPair = rankedUserCategories
                .stream()
                .filter(p -> p.getSecond().getUserCategoryPK().getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        Predicate<Pair<Integer, UserCategory>> topFilterPredicate = topPlayersOnly(numTopUsers);
        if (userCategoryPair != null) {
            topFilterPredicate = topPlayersAndNeighbours(numTopUsers, userCategoryPair.getFirst());
        }

        rankedUserCategoriesFiltered = rankedUserCategories.stream()
                .filter(topFilterPredicate)
                .collect(toList());

        return rankedUserCategoriesFiltered;
    }

    private Predicate<Pair<Integer, UserCategory>> topPlayersOnly(Integer topCount) {
        return p -> p.getFirst() <= topCount;
    }

    private Predicate<Pair<Integer, UserCategory>> topPlayersAndNeighbours(Integer topCount, Integer userRank) {
        return p -> (p.getFirst() <= topCount) || Math.abs(p.getFirst() - userRank) <= 1;
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
