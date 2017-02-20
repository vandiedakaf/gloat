package com.vdda.contest;

import com.vdda.elo.EloCalculator;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.SlackUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by francois
 * on 2017-02-19
 * for vandiedakaf solutions
 */
@Service
public class LossProcessor extends ContestProcessor {

    @Autowired
    public LossProcessor(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {
        super(contestRepository, userCategoryRepository, slackUtilities);
    }

    EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
        return eloCalculator.adjustedRating(EloCalculator.Outcome.LOSE);
    }

    void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
        reporterCategory.setLosses(opponentCategory.getLosses() + 1); // TODO does this create a race condition?
        opponentCategory.setWins(reporterCategory.getWins() + 1); // TODO does this create a race condition?
    }

}
