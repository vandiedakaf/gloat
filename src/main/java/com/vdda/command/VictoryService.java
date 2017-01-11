package com.vdda.command;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.vdda.action.Callbacks.VICTORY_CONFIRM;

/**
 * Created by francois
 * on 2016-12-30
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class VictoryService {

    private final RestTemplate restTemplate;
    private final SlackUtilities slackUtilities;

    @Autowired
    public VictoryService(RestTemplateBuilder restTemplateBuilder, SlackUtilities slackUtilities) {
        this.restTemplate = restTemplateBuilder.build();
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
            sendResponse(parameters.get("response_url"), response);
            return new AsyncResult<>(null);
        }

        // TODO don't allow @slackbot or self as a user

        Response response = confirmationButton(user.get());

        sendResponse(parameters.get("response_url"), response);

        return new AsyncResult<>(null);
    }

    private Response confirmationButton(User user) {
        Response response = new Response();
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        List<Action> actions = new ArrayList<>();
        Action actionYes = new Action();
        actionYes.setName("yes");
        actionYes.setText("Yes");
        actionYes.setValue("yes");
        actionYes.setStyle("primary");
        actions.add(actionYes);
        Action actionNo = new Action();
        actionNo.setName("no");
        actionNo.setText("No");
        actionNo.setValue("no");
        actionNo.setStyle("danger");
        actions.add(actionNo);
        attachment.setFallback("Victory Confirmation");
        attachment.setTitle("Victory Confirmation");
        attachment.setText("Did you defeat <@" + user.getId() + ">?");
        attachment.setColor("#86C53C");
        attachment.setCallback_id(callbackBuilder(VICTORY_CONFIRM.toString(), user.getId()));
        attachment.setActions(actions);
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }

    private String callbackBuilder(String callbackId, String userId) {
        return callbackId + "|" + userId;
    }


    private void sendResponse(String responseUrl, Response response) {
        restTemplate.postForLocation(responseUrl, response);
    }
}
