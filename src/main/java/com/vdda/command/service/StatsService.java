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

	private static final String COLOUR_GREEN = "#86C53C";

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
	public void processRequest(Request request) {

		final String teamId = request.getParameter(SlackParameters.TEAM_ID.toString());
		final String channelId = request.getParameter(SlackParameters.CHANNEL_ID.toString());

		Response response = new Response();

		String userId;

		List<String> args = request.getArguments();

		if (args.size() == 1) {
			userId = request.getParameter(SlackParameters.USER_ID.toString());
		} else {
			Optional<User> slackUser = slackUtilities.getUser(teamId, args.get(1));
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
		channelStats.setText("There have been " + categoryRepository.sumTotalPlayedByCategory(category.get().getId()) + " contests in this channel");
		channelStats.setColor(COLOUR_GREEN);
		attachments.add(channelStats);

		Attachment playerStats = new Attachment();
		playerStats.setFallback("Player Stats");
		playerStats.setTitle("Player Stats");
		playerStats.setText("The Win:Loss:Draw ratio for the enquired user is: " + userCategory.getWins() + ":" + userCategory.getLosses() + ":" + userCategory.getDraws());
		playerStats.setColor(COLOUR_GREEN);
		attachments.add(playerStats);

		response.setAttachments(attachments);

		restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
	}

}
