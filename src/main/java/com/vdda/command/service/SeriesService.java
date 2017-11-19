package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.ContestOutcome;
import com.vdda.slack.Action;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static com.vdda.callback.Callbacks.CONFIRM_SERIES;
import static com.vdda.command.Series.OUTCOME_ARGUMENT;
import static com.vdda.command.service.CallbackBuilder.callbackIdBuilder;

@Service
@Slf4j
public class SeriesService extends ContestService {

    @Autowired
    public SeriesService(RestTemplate restTemplate, SlackUtilities slackUtilities) {
        super(restTemplate, slackUtilities);
    }

    @Override
    protected Response confirmationButton(User user, List<String> arguments) {
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
        attachment.setFallback("Series Confirmation");
        attachment.setTitle("Series Confirmation");
        attachment.setText(constructConfirmationMessage(user, arguments));
        attachment.setColor("#86C53C");
        attachment.setCallback_id(callbackIdBuilder(CONFIRM_SERIES.toString(), user.getId(), arguments.get(OUTCOME_ARGUMENT).toLowerCase()));
        attachment.setActions(actions);
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }

    private String constructConfirmationMessage(User user, List<String> arguments) {
        // Split string into array of character strings: https://stackoverflow.com/a/12969483/792287
        String[] series = arguments.get(OUTCOME_ARGUMENT).toLowerCase().split("(?!^)");

        long winCount = Arrays.stream(series).filter(o -> (ContestOutcome.getEnumByKey(o) == ContestOutcome.WIN)).count();
        long lossCount = Arrays.stream(series).filter(o -> (ContestOutcome.getEnumByKey(o) == ContestOutcome.LOSS)).count();
        long drawCount = Arrays.stream(series).filter(o -> (ContestOutcome.getEnumByKey(o) == ContestOutcome.DRAW)).count();

        StringBuilder confirmationMessage = new StringBuilder("Confirm that you played " + series.length + " game" + (series.length > 1 ? "s" : "")
                + " against <@" + user.getId() + "> (");

        StringJoiner joiner = new StringJoiner(", ");
        if (winCount > 0) {
            String wins = winCount + " win";
            if (winCount > 1) {
                wins += "s";
            }
            joiner.add(wins);
        }
        if (lossCount > 0) {
            String losses = lossCount + " loss";
            if (lossCount > 1) {
                losses += "es";
            }
            joiner.add(losses);
        }
        if (drawCount > 0) {
            String draws = drawCount + " draw";
            if (drawCount > 1) {
                draws += "s";
            }
            joiner.add(draws);
        }

        confirmationMessage.append(joiner.toString());
        confirmationMessage.append(").");

        return confirmationMessage.toString();
    }
}
