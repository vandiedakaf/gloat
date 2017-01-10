package com.vdda.action;

import com.vdda.jpa.*;
import com.vdda.jpa.User;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
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
public class VictoryConfirm implements Callback {

    private final CategoryRepository categoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final ContestRepository contestRepository;
    private static final Long ELO_INIT = 1500L;

    @Autowired
    public VictoryConfirm(CategoryRepository categoryRepository, UserCategoryRepository userCategoryRepository, UserRepository userRepository, ContestRepository contestRepository) {
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.userRepository = userRepository;
        this.contestRepository = contestRepository;
    }

    @Override
    public Response run(CallbackRequest callbackRequest) {

        String actionName = callbackRequest.getActions().get(0).getName();
        String loserId = callbackRequest.getCallbackId().split("\\|")[1];

        if (actionName.equals("no")) {
            Response response = new Response();
            List<Attachment> attachments = new ArrayList<>();
            Attachment attachment = new Attachment();
            attachment.setTitle("Victory Confirmation");
            attachment.setText("Did you defeat <@" + loserId + ">?\nYou've opted not to confirm this victory.");
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

        User userWinner = getUser(winnerId, teamId, category.getId());
        User userLoser = getUser(loserId, teamId, category.getId());

        Contest contest = new Contest(category.getId(), userWinner.getId(), userLoser.getId());
        contestRepository.save(contest);

        // TODO post dispute message to loser

        Response response = new Response();
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setTitle("Victory Confirmation");
        attachment.setText("Did you defeat <@" + loserId + ">?\nCongratulations on your victory!\n<@" + loserId + "> has 8 hours to dispute your claim.");
        attachment.setColor("#86C53C");
        attachments.add(attachment);
        response.setAttachments(attachments);
        return response;
    }

    private User getUser(String userId, String teamId, Long categoryId) {
        User user = userRepository.findByTeamIdAndUserId(teamId, userId);
        if (user == null) {
            user = new User(teamId, userId);
            userRepository.save(user);
            UserCategory userCategory = new UserCategory(new UserCategoryPK(user.getId(), categoryId), ELO_INIT);
            userCategoryRepository.save(userCategory);
        }
        return user;
    }

    @Override
    public String getCallbackId() {
        return Callbacks.VICTORY_CONFIRM.toString();
    }

}
