package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.Action;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.vdda.callback.Callbacks.CONFIRM_LOSS;

/**
 * Created by francois
 * on 2016-12-30
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class LossService extends ContestService {

    @Autowired
    public LossService(RestTemplateBuilder restTemplateBuilder, SlackUtilities slackUtilities) {
        super(restTemplateBuilder.build(), slackUtilities);
    }

    @Override
    protected Response confirmationButton(User user) {
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
        attachment.setFallback("Loss Confirmation");
        attachment.setTitle("Loss Confirmation");
        attachment.setText("Confirm your loss to <@" + user.getId() + ">.");
        attachment.setColor("#86C53C");
        attachment.setCallback_id(callbackBuilder(CONFIRM_LOSS.toString(), user.getId()));
        attachment.setActions(actions);
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }
}
