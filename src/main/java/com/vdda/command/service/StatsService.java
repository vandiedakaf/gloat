package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.Category;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.jpa.UserUserCategory;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.repository.UserUserCategoryRepository;
import com.vdda.slack.*;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StatsService {

	private static final String COLOUR_GOLD = "#FFD700";
	private static final String COLOUR_GREEN = "#86C53C";

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
				restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
				return;
			}
		} else {
			slackUser = slackUtilities.getUserByUsername(teamId, args.get(1));
			if (!slackUser.isPresent()) {
				response.setText("Sorry, seems like " + args.get(1) + " is some imaginary person.");
				restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
				return;
			}
			userId = slackUser.get().getId();
		}

		Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);
		if (!category.isPresent()) {
			response.setText("No contests have been registered in this category.");
			restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
			return;
		}

		Optional<com.vdda.jpa.User> user = userRepository.findByTeamIdAndUserId(teamId, userId);
		if (!user.isPresent()) {
			response.setText("No contests have been registered for this user.");
			restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
			return;
		}

		UserCategoryPK userCategoryPK = new UserCategoryPK(user.get(), category.get().getId());
		UserCategory userCategory = userCategoryRepository.findOne(userCategoryPK);
		if (userCategory == null) {
			response.setText("No contests have been registered for this user.");
			restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
			return;
		}

		List<Attachment> attachments = new ArrayList<>();

		Attachment channelStats = new Attachment();
		channelStats.setFallback("Channel Stats");
		channelStats.setTitle("Channel Stats");

		List<Field> channelFields = new ArrayList<>();


		Field numberOfContests = new Field();
		numberOfContests.setTitle("Number of Contests");
		numberOfContests.setValue(Integer.toString(categoryRepository.sumTotalPlayedByCategory(category.get().getId())));
		numberOfContests.setShortMessage(true);
		channelFields.add(numberOfContests);

		Optional<UserCategory> maxStreakCount = userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByStreakCountDescStreakTypeDesc(category.get().getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= 10)
				.findFirst();
		if (maxStreakCount.isPresent()) {
			Field maxStreak = new Field();
			maxStreak.setTitle("Largest Streak");
			maxStreak.setValue("<@" + slackUtilities.getUserById(teamId, maxStreakCount.get().getUserCategoryPK().getUser().getUserId()).orElse(new User()).getName() + "> with " + maxStreakCount.get().getStreakCount() + " " + (maxStreakCount.get().getStreakCount() > 1 ? maxStreakCount.get().getStreakType().getPlural() : maxStreakCount.get().getStreakType().name().toLowerCase()));
			maxStreak.setShortMessage(true);

			channelFields.add(maxStreak);
		}

		channelStats.setFields(channelFields);
		channelStats.setColor(COLOUR_GREEN);
		attachments.add(channelStats);

		Attachment playerStats = new Attachment();
		playerStats.setFallback("Player Stats");
		playerStats.setTitle("Player Stats: <@" + slackUser.get().getName() + ">");

		List<Field> playerFields = new ArrayList<>();

		Field playerWLD = new Field();
		playerWLD.setTitle("Win:Loss:Draw");
		playerWLD.setValue(userCategory.getWins() + ":" + userCategory.getLosses() + ":" + userCategory.getDraws());
		playerWLD.setShortMessage(true);
		playerFields.add(playerWLD);

		Field playerStreak = new Field();
		playerStreak.setTitle("Streak");
		playerStreak.setValue(userCategory.getStreakCount() + " " + (userCategory.getStreakCount() > 1 ? userCategory.getStreakType().getPlural() : userCategory.getStreakType().name().toLowerCase()));
		playerStreak.setShortMessage(true);
		playerFields.add(playerStreak);

		// TODO replace literal "10" with global config
		Optional<UserUserCategory> frenemy = userUserCategoryRepository.findWilsonMax(user.get().getId(), category.get().getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= 10)
				.findFirst();
		if (frenemy.isPresent()) {
			Field playerWilsonHigh = new Field();
			playerWilsonHigh.setTitle("Frenemy @" + slackUtilities.getUserById(teamId, frenemy.get().getUserUserCategoryPK().getOpponent().getUserId()).orElse(new User()).getName());
			playerWilsonHigh.setValue(String.format("Wilson Score = %.2f\nW:L:D = %d:%d:%d", frenemy.get().getWilson(), frenemy.get().getWins(), frenemy.get().getLosses(), frenemy.get().getDraws()));
			playerWilsonHigh.setShortMessage(true);
			playerFields.add(playerWilsonHigh);
		}

		// TODO replace literal "10" with global config
		Optional<UserUserCategory> nemesis = userUserCategoryRepository.findWilsonMin(user.get().getId(), category.get().getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= 10)
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
		attachments.add(playerStats);

		response.setAttachments(attachments);

		restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
	}

}
