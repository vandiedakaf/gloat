package com.vdda.callback;

import com.vdda.EnvProperties;
import com.vdda.contest.ContestResolver;
import com.vdda.jpa.*;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class ConfirmContestVictory extends ConfirmContest {

    @Autowired
    public ConfirmContestVictory(EnvProperties envProperties, CategoryRepository categoryRepository, UserRepository userRepository, ContestRepository contestRepository, ContestResolver contestResolver, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {

        super(envProperties, categoryRepository, userRepository, contestRepository, contestResolver, userCategoryRepository, slackUtilities);
    }

    @Override
    protected Attachment confirmAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Victory Confirmation");
        attachment.setText("Confirm that you beat <@" + opponentId + ">.\nCongratulations on your victory!");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected Attachment denyAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Victory Confirmation");
        attachment.setText("Confirm that you beat <@" + opponentId + ">.\nYou've opted not to confirm this victory.");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected ContestOutcome getContestOutcome() {
        return ContestOutcome.WIN;
    }

    @Override
    protected void notifyChannelBefore(Contest contest) {
        UserCategoryPK reporterCategoryPK = new UserCategoryPK(contest.getReporter(), contest.getCategory().getId());
        UserCategory userCategoryReporter = getOrCreateUserCategory(reporterCategoryPK);

        UserCategoryPK opponentCategoryPK = new UserCategoryPK(contest.getOpponent(), contest.getCategory().getId());
        UserCategory userCategoryOpponent = getOrCreateUserCategory(opponentCategoryPK);

        String message = "<@" + contest.getReporter().getUserId() + "> (" + userCategoryReporter.getElo() + ") is gloating about their victory over <@" + contest.getOpponent().getUserId() + "> (" + userCategoryOpponent.getElo() + ") :tada:";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

    @Override
    public String getCallbackId() {
        return Callbacks.CONFIRM_VICTORY.toString();
    }

}
