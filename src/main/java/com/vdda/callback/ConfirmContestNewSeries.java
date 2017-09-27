package com.vdda.callback;

import com.vdda.EnvProperties;
import com.vdda.contest.ContestResolver;
import com.vdda.jpa.*;
import com.vdda.jpa.User;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConfirmContestNewSeries extends ConfirmContestNew {

    private final ContestRepository contestRepository;

    Contest contest;
    private List<String> seriesOutcomeList;

    @Autowired
    public ConfirmContestNewSeries(EnvProperties envProperties, CategoryRepository categoryRepository, UserRepository userRepository, ContestRepository contestRepository, ContestResolver contestResolver, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {

        super(envProperties, categoryRepository, userRepository, contestResolver, userCategoryRepository, slackUtilities);
        this.contestRepository = contestRepository;
    }

    @Override
    protected Attachment confirmAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Series Confirmation");
        attachment.setText("You've confirmed this series.");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected Attachment denyAttachment(String opponentId) {
        Attachment attachment = new Attachment();
        attachment.setTitle("Series Confirmation");
        attachment.setText("You've opted not to confirm this series.");
        attachment.setColor("#86C53C");

        return attachment;
    }

    @Override
    protected void notifyChannelBefore() {
        UserCategoryPK reporterCategoryPK = new UserCategoryPK(contest.getReporter(), contest.getCategory().getId());
        UserCategory userCategoryReporter = getOrCreateUserCategory(reporterCategoryPK);

        UserCategoryPK opponentCategoryPK = new UserCategoryPK(contest.getOpponent(), contest.getCategory().getId());
        UserCategory userCategoryOpponent = getOrCreateUserCategory(opponentCategoryPK);

        String message = "<@" + contest.getReporter().getUserId() + "> (" + userCategoryReporter.getElo() + ") has competed against <@" + contest.getOpponent().getUserId() + "> (" + userCategoryOpponent.getElo() + ") with the outcome: "+ String.join(", ", seriesOutcomeList) +".";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

    @Override
    public String getCallbackId() {
        return Callbacks.CONFIRM_SERIES.toString();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void persistContests(Category category, User reporter, User opponent) {

        // https://stackoverflow.com/a/12969483/792287
        String[] seriesOutcome = callbackRequest.getCallbackId().split("\\|")[2].split("(?!^)");
        
        seriesOutcomeList = new ArrayList<>();

        for (String outcome : seriesOutcome) {
            seriesOutcomeList.add(ContestOutcome.getEnumByKey(outcome).toString());

            contest = new Contest(category, reporter, opponent, ContestOutcome.getEnumByKey(outcome));
            contest = contestRepository.save(contest); // save the outcome of the last contest for use by notifyChannelBefore()
        }
    }
}
