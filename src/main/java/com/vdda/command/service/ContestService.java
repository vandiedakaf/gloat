package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public abstract class ContestService {

	private final RestTemplate restTemplate;
	private final SlackUtilities slackUtilities;

	public ContestService(RestTemplate restTemplate, SlackUtilities slackUtilities) {
		this.restTemplate = restTemplate;
		this.slackUtilities = slackUtilities;
	}

	@Async
	public void processRequest(Request request) {

		List<String> args = request.getArguments();

		final String teamId = request.getParameter(SlackParameters.TEAM_ID.toString());

		Optional<User> user = slackUtilities.getUserByUsername(teamId, args.get(0));

		if (!user.isPresent()) {
			Response response = new Response();
			response.setText("Sorry, seems like " + args.get(0) + " is some imaginary person.");
			restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
			return;
		}

		if ("slackbot".equals(args.get(0))
				|| "@slackbot".equals(args.get(0))
				|| request.getParameter(SlackParameters.USER_ID.toString()).equals(user.get().getId())) {
			Response response = new Response();
			response.setText("Sorry, you can't compete against yourself or slackbot.");
			restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
			return;
		}

		Response response = confirmationButton(user.get(), args);

		restTemplate.postForLocation(request.getParameter(SlackParameters.RESPONSE_URL.toString()), response);
	}

	abstract Response confirmationButton(User user, List<String> contestArguments);
}
