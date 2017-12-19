package com.vdda.command.service;

import com.vdda.domain.jpa.Category;
import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GloatService {

	private final RestTemplate restTemplate;
	private final UserCategoryRepository userCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final SlackUtilities slackUtilities;

	@Value("${gloat.calibration}")
	private int calibration;

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

		List<UserCategory> userCategories = userCategoryRepository.findAllByUserCategoryPK_CategoryIdOrderByEloDesc(category.get().getId())
				.stream()
				.filter(u -> (u.getWins() + u.getLosses() + u.getDraws()) >= calibration)
				.collect(Collectors.toList());

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
