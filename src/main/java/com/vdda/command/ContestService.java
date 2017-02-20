package com.vdda.command;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Created by francois
 * on 2016-12-30
 * for vandiedakaf solutions
 */
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
    Future<?> processRequest(Map<String, String> parameters, List<String> args) {

        final String teamId = parameters.get(SlackParameters.TEAM_ID.toString());

        Optional<User> user = slackUtilities.getUser(teamId, args.get(0));

        if (!user.isPresent()) {
            log.debug("User not found");
            Response response = new Response();
            response.setText("Sorry, seems like " + args.get(0) + " is some imaginary person.");
            sendResponse(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return new AsyncResult<>(null);
        }

        if ("slackbot".equals(args.get(0))
                || "@slackbot".equals(args.get(0))
                || parameters.get(SlackParameters.USER_ID.toString()).equals(user.get().getId())) {
            Response response = new Response();
            response.setText("Sorry, you can't compete against yourself of slackbot.");
            sendResponse(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);
            return new AsyncResult<>(null);
        }

        Response response = confirmationButton(user.get());

        sendResponse(parameters.get(SlackParameters.RESPONSE_URL.toString()), response);

        return new AsyncResult<>(null);
    }

    protected abstract Response confirmationButton(User user);

    String callbackBuilder(String callbackId, String userId) {
        return callbackId + "|" + userId;
    }

    private void sendResponse(String responseUrl, Response response) {
        restTemplate.postForLocation(responseUrl, response);
    }
}
