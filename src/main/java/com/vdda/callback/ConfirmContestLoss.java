package com.vdda.callback;

import com.vdda.EnvProperties;
import com.vdda.contest.ContestResolver;
import com.vdda.jpa.Contest;
import com.vdda.jpa.ContestOutcome;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfirmContestLoss extends ConfirmContest {

    @Autowired
    public ConfirmContestLoss(EnvProperties envProperties, CategoryRepository categoryRepository, UserRepository userRepository, ContestRepository contestRepository, ContestResolver contestResolver, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {

        super(envProperties, categoryRepository, userRepository, contestRepository, contestResolver, userCategoryRepository, slackUtilities);
    }

    @Override
    protected Attachment confirmAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Loss Confirmation");
        attachment.setText("Confirm your loss to <@" + opponentId + ">.\nBetter luck next time.");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected Attachment denyAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Loss Confirmation");
        attachment.setText("Confirm your loss to <@" + opponentId + ">.\nYou've opted not to confirm this loss.");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected ContestOutcome getContestOutcome() {
        return ContestOutcome.LOSS;
    }

    @Override
    protected void notifyChannelBefore(Contest contest) {
        UserCategoryPK reporterCategoryPK = new UserCategoryPK(contest.getReporter(), contest.getCategory().getId());
        UserCategory userCategoryReporter = getOrCreateUserCategory(reporterCategoryPK);

        UserCategoryPK opponentCategoryPK = new UserCategoryPK(contest.getOpponent(), contest.getCategory().getId());
        UserCategory userCategoryOpponent = getOrCreateUserCategory(opponentCategoryPK);

        String message = "<@" + contest.getReporter().getUserId() + "> (" + userCategoryReporter.getElo() + ") has lost to <@" + contest.getOpponent().getUserId() + "> (" + userCategoryOpponent.getElo() + ") :face_with_head_bandage:";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

    @Override
    public String getCallbackId() {
        return Callbacks.CONFIRM_LOSS.toString();
    }

}
