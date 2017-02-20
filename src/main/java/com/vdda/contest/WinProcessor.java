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
public class WinProcessor extends ContestProcessor {

    @Autowired
    public WinProcessor(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {
        super(contestRepository, userCategoryRepository, slackUtilities);
    }

    EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
        return eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);
    }

    void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
        // TODO
        // http://www.byteslounge.com/tutorials/spring-transaction-isolation-tutorial
        // http://stackoverflow.com/questions/8490852/spring-transactional-isolation-propagation
        reporterCategory.setWins(reporterCategory.getWins() + 1); // TODO does this create a race condition?
        opponentCategory.setLosses(opponentCategory.getLosses() + 1); // TODO does this create a race condition?
    }

}
