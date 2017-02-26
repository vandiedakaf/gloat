package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.Category;
import com.vdda.jpa.Contest;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.SlackUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by francois
 * on 2017-02-19
 * for vandiedakaf solutions
 */
@Service
public abstract class ContestProcessor {

    private EnvProperties envProperties;
    private ContestRepository contestRepository;
    private UserCategoryRepository userCategoryRepository;
    private SlackUtilities slackUtilities;

    @Autowired
    public ContestProcessor(EnvProperties envProperties, ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {
        this.envProperties = envProperties;
        this.contestRepository = contestRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.slackUtilities = slackUtilities;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void processContest(Contest contest) {

        Category category = contest.getCategory();

        UserCategoryPK reporterCategoryPK = new UserCategoryPK(contest.getReporter(), category.getId());
        UserCategory userCategoryReporter = getOrCreateUserCategory(reporterCategoryPK);

        UserCategoryPK opponentCategoryPK = new UserCategoryPK(contest.getOpponent(), category.getId());
        UserCategory userCategoryOpponent = getOrCreateUserCategory(opponentCategoryPK);

        EloCalculator eloCalculator = new EloCalculator(userCategoryReporter.getElo(), userCategoryOpponent.getElo(), Math.min(userCategoryReporter.getK(), userCategoryOpponent.getK()));

        EloCalculator.Ratings ratings = getRatings(eloCalculator);

        userCategoryReporter.setElo((int) Math.round(ratings.getReporterRating()));
        userCategoryReporter.setK(EloCalculator.determineK(userCategoryReporter.getWins() + userCategoryReporter.getLosses() + userCategoryReporter.getDraws()));

        userCategoryOpponent.setElo((int) Math.round(ratings.getOpponentRating()));
        userCategoryOpponent.setK(EloCalculator.determineK(userCategoryOpponent.getWins() + userCategoryOpponent.getLosses() + userCategoryOpponent.getDraws()));

        adjustUserCategoryStats(userCategoryReporter, userCategoryOpponent);

        userCategoryReporter = userCategoryRepository.save(userCategoryReporter);
        userCategoryOpponent = userCategoryRepository.save(userCategoryOpponent);

        contest.setProcessed(true);
        contest = contestRepository.save(contest);

        notifyChannelAfter(contest, userCategoryReporter, userCategoryOpponent);
    }

    private UserCategory getOrCreateUserCategory(UserCategoryPK userCategoryPK) {
        UserCategory userCategory = userCategoryRepository.findOne(userCategoryPK);
        if (userCategory == null) {
            userCategory = new UserCategory(userCategoryPK);
            userCategory.setElo(envProperties.getEloInit());
            userCategory = userCategoryRepository.save(userCategory);
        }
        return userCategory;
    }

    private void notifyChannelAfter(Contest contest, UserCategory userCategoryReporter, UserCategory userCategoryOpponent) {

        String message = "\nTheir new ratings are <@" + contest.getReporter().getUserId() + "> (" + userCategoryReporter.getElo() + ") and <@" + contest.getOpponent().getUserId() + "> (" + userCategoryOpponent.getElo() + ").";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

    abstract EloCalculator.Ratings getRatings(EloCalculator eloCalculator);

    abstract void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory);
}