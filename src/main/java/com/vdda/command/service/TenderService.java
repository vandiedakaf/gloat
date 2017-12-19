package com.vdda.command.service;

import com.vdda.domain.jpa.Category;
import com.vdda.domain.jpa.User;
import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.jpa.UserCategoryPK;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserRepository;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import com.vdda.tool.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TenderService {

    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SlackUtilities slackUtilities;

    @Autowired
    public TenderService(UserCategoryRepository userCategoryRepository, UserRepository userRepository, CategoryRepository categoryRepository, SlackUtilities slackUtilities) {
        this.userCategoryRepository = userCategoryRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.slackUtilities = slackUtilities;
    }

    @Async
    public void processRequest(Request request) {

        tender(request);
    }

    private void tender(Request request) {

        Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.CHANNEL_ID.toString()));

        StringBuilder tenderMessage = new StringBuilder();
        tenderMessage.append("<@");
        tenderMessage.append(request.getParameter(SlackParameters.USER_ID.toString()));
        tenderMessage.append(">");

        if (!category.isPresent()) {
            noRank(request, tenderMessage);
            return;
        }

        Optional<User> user = userRepository.findByTeamIdAndUserId(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.USER_ID.toString()));

        if (!user.isPresent()) {
            noRank(request, tenderMessage);
            return;
        }

        UserCategoryPK userCategoryPK = new UserCategoryPK(user.get(), category.get().getId());
        Optional<UserCategory> userCategory = userCategoryRepository.findUserCategoryByUserCategoryPK(userCategoryPK);

        if (!userCategory.isPresent()) {
            noRank(request, tenderMessage);
            return;
        }

        tenderMessage.append(" (");
        tenderMessage.append(userCategory.get().getElo());
        tenderMessage.append(") is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.CHANNEL_ID.toString()), tenderMessage.toString());
    }

    private void noRank(Request request, StringBuilder tenderDetails) {
        tenderDetails.append(" (no rank) is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(request.getParameter(SlackParameters.TEAM_ID.toString()), request.getParameter(SlackParameters.CHANNEL_ID.toString()), tenderDetails.toString());
    }
}
