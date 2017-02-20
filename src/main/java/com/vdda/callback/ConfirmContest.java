package com.vdda.callback;

import com.vdda.contest.ContestResolver;
import com.vdda.jpa.*;
import com.vdda.jpa.User;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
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
public abstract class ConfirmContest implements Callback {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ContestRepository contestRepository;
    private final ContestResolver contestResolver;
    final UserCategoryRepository userCategoryRepository;
    final SlackUtilities slackUtilities;

    public ConfirmContest(CategoryRepository categoryRepository, UserRepository userRepository, ContestRepository contestRepository, ContestResolver contestResolver, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.contestRepository = contestRepository;
        this.contestResolver = contestResolver;
        this.userCategoryRepository = userCategoryRepository;
        this.slackUtilities = slackUtilities;
    }

    @Override
    public Response run(CallbackRequest callbackRequest) {

        String actionName = callbackRequest.getActions().get(0).getName();
        String opponentId = callbackRequest.getCallbackId().split("\\|")[1];

        if ("no".equals(actionName)) {
            Response response = new Response();
            List<Attachment> attachments = new ArrayList<>();
            attachments.add(denyAttachment(opponentId));
            response.setAttachments(attachments);
            return response;
        }

        String reporterId = callbackRequest.getUser().getId();
        String channelId = callbackRequest.getChannel().getId();
        String teamId = callbackRequest.getTeam().getId();
        Category category = getOrCreateByTeamIdAndChannelId(teamId, channelId);

        User reporter = getOrCreateUser(teamId, reporterId);
        User opponent = getOrCreateUser(teamId, opponentId);

        Contest contest = new Contest(category, reporter, opponent, getContestOutcome());
        contest = contestRepository.save(contest);
        notifyChannelBefore(contest);

        contestResolver.processContests();

        Response response = new Response();
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(confirmAttachment(opponentId));
        response.setAttachments(attachments);
        return response;
    }

    private User getOrCreateUser(String teamId, String userId) {
        User user = userRepository.findByTeamIdAndUserId(teamId, userId);
        if (user == null) {
            user = new User(teamId, userId);
            user = userRepository.save(user);
        }
        return user;
    }

    private Category getOrCreateByTeamIdAndChannelId(String teamId, String channelId) {
        Category category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);
        if (category == null) {
            category = new Category(teamId, channelId);
            category = categoryRepository.save(category);
        }
        return category;
    }

    UserCategory getOrCreateUserCategory(UserCategoryPK userCategoryPK) {
        UserCategory userCategory = userCategoryRepository.findOne(userCategoryPK);
        if (userCategory == null) {
            userCategory = new UserCategory(userCategoryPK);
            userCategory = userCategoryRepository.save(userCategory);
        }
        return userCategory;
    }

    protected abstract Attachment confirmAttachment(String opponentId);

    protected abstract Attachment denyAttachment(String opponentId);

    protected abstract ContestOutcome getContestOutcome();

    protected abstract void notifyChannelBefore(Contest contest);

}
