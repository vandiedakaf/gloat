package com.vdda.command.service;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
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
    public void processRequest(Map<String, String> parameters) {

        tender(parameters);
    }

    private void tender(Map<String, String> parameters) {

        Optional<Category> category = categoryRepository.findByTeamIdAndChannelId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()));

        StringBuilder tenderMessage = new StringBuilder();
        tenderMessage.append("<@");
        tenderMessage.append(parameters.get(SlackParameters.USER_ID.toString()));
        tenderMessage.append(">");

        if (!category.isPresent()) {
            noRank(parameters, tenderMessage);
            return;
        }

        Optional<User> user = userRepository.findByTeamIdAndUserId(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.USER_ID.toString()));

        if (!user.isPresent()) {
            noRank(parameters, tenderMessage);
            return;
        }

        UserCategoryPK userCategoryPK = new UserCategoryPK(user.get(), category.get().getId());
        Optional<UserCategory> userCategory = userCategoryRepository.findUserCategoryByUserCategoryPK(userCategoryPK);

        if (!userCategory.isPresent()) {
            noRank(parameters, tenderMessage);
            return;
        }

        tenderMessage.append(" (");
        tenderMessage.append(userCategory.get().getElo());
        tenderMessage.append(") is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()), tenderMessage.toString());
    }

    private void noRank(Map<String, String> parameters, StringBuilder tenderDetails) {
        tenderDetails.append(" (no rank) is looking for a challenger :mega:");
        slackUtilities.sendChatMessage(parameters.get(SlackParameters.TEAM_ID.toString()), parameters.get(SlackParameters.CHANNEL_ID.toString()), tenderDetails.toString());
    }
}
