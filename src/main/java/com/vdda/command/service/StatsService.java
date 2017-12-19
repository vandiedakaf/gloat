package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.domain.jpa.Category;
import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.jpa.UserCategoryPK;
import com.vdda.domain.jpa.UserUserCategory;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserRepository;
import com.vdda.domain.repository.UserUserCategoryRepository;
import com.vdda.slack.*;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class StatsService {

	private static final String COLOUR_GREEN = "#86C53C";
	private static final String COLOUR_GOLD = "#FFD700";
	private static final int NUM_TOP_USERS = 3;

	@Value("${gloat.calibration}")
	private int calibration;

	private final RestTemplate restTemplate;
	private final SlackUtilities slackUtilities;
	private final UserCategoryRepository userCategoryRepository;
	private final UserUserCategoryRepository userUserCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;

	@Autowired
	public StatsService(RestTemplate restTemplate, SlackUtilities slackUtilities, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
		this.restTemplate = restTemplate;
		this.slackUtilities = slackUtilities;
		this.userCategoryRepository = userCategoryRepository;
		this.userUserCategoryRepository = userUserCategoryRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
	}

	@Async
	public void processRequest(Request request) {

		Response response = getStats(request);

		restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
	}

	private Response getStats(Request request) {

		final String teamId = request.getParameter(SlackParameters.TEAM_ID.toString());
		final String channelId = request.getParameter(SlackParameters.CHANNEL_ID.toString());

		Response response = new Response();

		String userId;

		List<String> args = request.getArguments();

		Optional<User> slackUser;

		if (args.size() == 1) {
			userId = request.getParameter(SlackParameters.USER_ID.toString());
			slackUser = slackUtilities.getUserById(teamId, userId);
			if (!slackUser.isPresent()) {
				response.setText("Strange, it seems like you don't exist on slack.");
				return response;
			}
		} else {
			slackUser = slackUtilities.getUserByUsername(teamId, args.get(1));
			if (!slackUser.isPresent()) {
				response.setText("Sorry, seems like " + args.get(1) + " is some imaginary person.");
				return response;
			}
			userId = slackUser.get().getId();
		}

		Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);
		if (!category.isPresent()) {
			response.setText("No contests have been registered in this category.");
			return response;
		}

		Optional<com.vdda.domain.jpa.User> user = userRepository.findByTeamIdAndUserId(teamId, userId);
		if (!user.isPresent()) {
			response.setText("No contests have been registered for this user.");
			return response;
		}

		UserCategoryPK userCategoryPK = new UserCategoryPK(user.get(), category.get().getId());
		UserCategory userCategory = userCategoryRepository.findOne(userCategoryPK);
		if (userCategory == null) {
			response.setText("No contests have been registered for this user.");
			return response;
		}

		List<Attachment> attachments = new ArrayList<>();
		attachments.add(getChannelStats(teamId, category.get(), request.getParameter(SlackParameters.USER_ID.toString())));
		attachments.add(getPlayerStats(teamId, slackUser.get(), category.get(), user.get(), userCategory));
		response.setAttachments(attachments);

		return response;
	}

	private Attachment getChannelStats(String teamId, Category category, String userId) {
		Attachment channelStats = new Attachment();
		channelStats.setFallback("Channel Stats");
		channelStats.setTitle("Channel Stats");
		channelStats.setColor(COLOUR_GREEN);

		List<Field> channelFields = new ArrayList<>();

		Field numberOfContests = new Field();
		numberOfContests.setTitle("Number of Contests");
		numberOfContests.setValue(Integer.toString(categoryRepository.sumTotalPlayedByCategory(category.getId())));
		numberOfContests.setShortMessage(true);
		channelFields.add(numberOfContests);

		Optional<UserCategory> maxStreakUserCategory = userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByStreakCountDescStreakTypeDescModifiedDesc(category.getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= calibration)
				.findFirst();
		if (maxStreakUserCategory.isPresent()) {
			Field maxStreak = new Field();
			maxStreak.setTitle("Longest Streak");
			maxStreak.setValue("<@" + slackUtilities.getUserById(teamId, maxStreakUserCategory.get().getUserCategoryPK().getUser().getUserId()).orElse(new User()).getName() + "> with " + maxStreakUserCategory.get().getStreakCount() + " " + (maxStreakUserCategory.get().getStreakCount() > 1 ? maxStreakUserCategory.get().getStreakType().getPlural() : maxStreakUserCategory.get().getStreakType().name().toLowerCase()));
			maxStreak.setShortMessage(true);

			channelFields.add(maxStreak);
		}

		List<UserCategory> userCategories = userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(category.getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= calibration)
				.collect(Collectors.toList());

		if (!userCategories.isEmpty()) {
			List<Pair<Integer, UserCategory>> rankedUserCategories = determineRankedPairs(userCategories);

			List<Pair<Integer, UserCategory>> rankedUserCategoriesFiltered = getFilteredList(userId, rankedUserCategories, NUM_TOP_USERS);

			boolean containsDraws = rankedUserCategoriesFiltered.stream()
					.mapToInt(u -> u.getSecond().getDraws())
					.sum() > 0;

			Field topField = new Field();
			if (containsDraws) {
				topField.setTitle("Rank. (Rating) Name [Wins-Losses-Draws]");
			} else {
				topField.setTitle("Rank. (Rating) Name [Wins-Losses]");
			}

			StringBuilder textTopContestants = new StringBuilder();
			rankedUserCategoriesFiltered.stream()
					.filter(p -> p.getFirst() <= NUM_TOP_USERS)
					.collect(toList())
					.forEach(u -> addContestant(u, textTopContestants, containsDraws));

			topField.setValue(textTopContestants.toString());

			channelFields.add(topField);

			if (rankedUserCategoriesFiltered.size() > NUM_TOP_USERS) {
				Field neighbourField = new Field();
				StringBuilder textNeighbouringContestants = new StringBuilder();
				rankedUserCategoriesFiltered.stream()
						.filter(p -> p.getFirst() > NUM_TOP_USERS)
						.collect(toList())
						.forEach(u -> addContestant(u, textNeighbouringContestants, containsDraws));

				neighbourField.setValue(textNeighbouringContestants.toString());
				channelFields.add(neighbourField);
			}

			channelStats.setFields(channelFields);
		}

		return channelStats;
	}

	private Attachment getPlayerStats(String teamId, User slackUser, Category category, com.vdda.domain.jpa.User user, UserCategory userCategory) {
		Attachment playerStats = new Attachment();
		playerStats.setFallback("Player Stats");
		playerStats.setTitle("Player Stats: <@" + slackUser.getName() + ">");

		List<Field> playerFields = new ArrayList<>();

		Field playerWLD = new Field();
		playerWLD.setTitle("Win:Loss:Draw");
		playerWLD.setValue(userCategory.getWins() + ":" + userCategory.getLosses() + ":" + userCategory.getDraws());
		playerWLD.setShortMessage(true);
		playerFields.add(playerWLD);

		Field playerStreak = new Field();
		playerStreak.setTitle("Longest Streak");
		playerStreak.setValue(userCategory.getStreakCount() + " " + (userCategory.getStreakCount() > 1 ? userCategory.getStreakType().getPlural() : userCategory.getStreakType().name().toLowerCase()));
		playerStreak.setShortMessage(true);
		playerFields.add(playerStreak);

		Optional<UserUserCategory> frenemy = userUserCategoryRepository.findWilsonMax(user.getId(), category.getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= calibration)
				.findFirst();
		if (frenemy.isPresent()) {
			Field playerWilsonHigh = new Field();
			playerWilsonHigh.setTitle("Frenemy @" + slackUtilities.getUserById(teamId, frenemy.get().getUserUserCategoryPK().getOpponent().getUserId()).orElse(new User()).getName());
			playerWilsonHigh.setValue(String.format("Wilson Score = %.2f\nW:L:D = %d:%d:%d", frenemy.get().getWilson(), frenemy.get().getWins(), frenemy.get().getLosses(), frenemy.get().getDraws()));
			playerWilsonHigh.setShortMessage(true);
			playerFields.add(playerWilsonHigh);
		}

		Optional<UserUserCategory> nemesis = userUserCategoryRepository.findWilsonMin(user.getId(), category.getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= calibration)
				.findFirst();
		if (nemesis.isPresent()) {
			Field playerWilsonLow = new Field();
			playerWilsonLow.setTitle("Nemesis @" + slackUtilities.getUserById(teamId, nemesis.get().getUserUserCategoryPK().getOpponent().getUserId()).orElse(new User()).getName());
			playerWilsonLow.setValue(String.format("Wilson Score = %.2f\nW:L:D = %d:%d:%d", nemesis.get().getWilson(), nemesis.get().getWins(), nemesis.get().getLosses(), nemesis.get().getDraws()));
			playerWilsonLow.setShortMessage(true);
			playerFields.add(playerWilsonLow);
		}

		playerStats.setFields(playerFields);

		playerStats.setColor(COLOUR_GOLD);
		return playerStats;
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
