package com.vdda.contest;

import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.jpa.UserUserCategory;
import com.vdda.domain.repository.ContestRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserUserCategoryRepository;
import com.vdda.elo.EloCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LossProcessor extends ContestProcessor {

    @Autowired
    public LossProcessor(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
        super(contestRepository, userCategoryRepository, userUserCategoryRepository);
    }

    @Override
    EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
        return eloCalculator.adjustedRating(EloCalculator.Outcome.LOSE);
    }

    @Override
    void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
        reporterCategory.setLosses(reporterCategory.getLosses() + 1);
        opponentCategory.setWins(opponentCategory.getWins() + 1);
    }

    @Override
    void adjustUserUserCategoryStats(UserUserCategory userUserCategoryReporter, UserUserCategory userUserCategoryOpponent) {
        userUserCategoryReporter.setLosses(userUserCategoryReporter.getLosses() + 1);
        userUserCategoryOpponent.setWins(userUserCategoryOpponent.getWins() + 1);
    }

}
