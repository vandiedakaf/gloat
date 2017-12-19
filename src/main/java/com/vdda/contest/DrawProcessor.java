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
public class DrawProcessor extends ContestProcessor {

    @Autowired
    public DrawProcessor(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
        super(contestRepository, userCategoryRepository, userUserCategoryRepository);
    }

    @Override
    EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
        return eloCalculator.adjustedRating(EloCalculator.Outcome.DRAW);
    }

    @Override
    void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
        reporterCategory.setDraws(reporterCategory.getDraws() + 1);
        opponentCategory.setDraws(opponentCategory.getDraws() + 1);
    }

    @Override
    void adjustUserUserCategoryStats(UserUserCategory userUserCategoryReporter, UserUserCategory userUserCategoryOpponent) {
        userUserCategoryReporter.setDraws(userUserCategoryReporter.getDraws() + 1);
        userUserCategoryOpponent.setDraws(userUserCategoryOpponent.getDraws() + 1);
    }
}
