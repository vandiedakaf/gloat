package com.vdda.command.service;

import com.github.seratch.jslack.api.model.User;
import com.vdda.slack.Action;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.vdda.callback.Callbacks.CONFIRM_DRAW;
import static com.vdda.command.service.CallbackBuilder.callbackIdBuilder;

@Service
@Slf4j
public class DrawService extends ContestService {

    @Autowired
    public DrawService(RestTemplate restTemplate, SlackUtilities slackUtilities) {
        super(restTemplate, slackUtilities);
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
        attachment.setFallback("Draw Confirmation");
        attachment.setTitle("Draw Confirmation");
        attachment.setText("Confirm that you drew with <@" + user.getId() + ">.");
        attachment.setColor("#86C53C");
        attachment.setCallback_id(callbackIdBuilder(CONFIRM_DRAW.toString(), user.getId()));
        attachment.setActions(actions);
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }
}
