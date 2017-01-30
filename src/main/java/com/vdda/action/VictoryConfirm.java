package com.vdda.action;

import com.vdda.elo.EloService;
import com.vdda.jpa.Category;
import com.vdda.jpa.Contest;
import com.vdda.jpa.User;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class VictoryConfirm implements Callback {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ContestRepository contestRepository;
    private final EloService eloService;

    @Autowired
    public VictoryConfirm(CategoryRepository categoryRepository, UserRepository userRepository, ContestRepository contestRepository, EloService eloService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.contestRepository = contestRepository;
        this.eloService = eloService;
    }

    @Override
    public Response run(CallbackRequest callbackRequest) {

        String actionName = callbackRequest.getActions().get(0).getName();
        String loserId = callbackRequest.getCallbackId().split("\\|")[1];

        if ("no".equals(actionName)) {
            Response response = new Response();
            List<Attachment> attachments = new ArrayList<>();
            Attachment attachment = new Attachment();
            attachment.setTitle("Victory Confirmation");
            attachment.setText("Confirm that you beat <@" + loserId + ">.\nYou've opted not to confirm this victory.");
            attachment.setColor("#86C53C");
            attachments.add(attachment);
            response.setAttachments(attachments);
            return response;
        }

        String winnerId = callbackRequest.getUser().getId();
        String channelId = callbackRequest.getChannel().getId();
        String teamId = callbackRequest.getTeam().getId();
        Category category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);
        if (category == null) {
            category = new Category(teamId, channelId);
            categoryRepository.save(category);
        }

        User winner = getUser(winnerId, teamId);
        User loser = getUser(loserId, teamId);

        Contest contest = new Contest(category, winner, loser);
        contestRepository.save(contest);

        eloService.processContests();

        Response response = new Response();
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setTitle("Victory Confirmation");
        attachment.setText("Confirm that you beat <@" + loserId + ">.\nCongratulations on your victory!");
        attachment.setColor("#86C53C");
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }

    private User getUser(String userId, String teamId) {
        User user = userRepository.findByTeamIdAndUserId(teamId, userId);
        if (user == null) {
            user = new User(teamId, userId);
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public String getCallbackId() {
        return Callbacks.VICTORY_CONFIRM.toString();
    }

}
