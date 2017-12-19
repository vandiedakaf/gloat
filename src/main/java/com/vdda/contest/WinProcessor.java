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
public class WinProcessor extends ContestProcessor {

    @Autowired
    public WinProcessor(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
        super(contestRepository, userCategoryRepository, userUserCategoryRepository);
    }

    @Override
    EloCalculator.Ratings getRatings(EloCalculator eloCalculator) {
        return eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);
    }

    @Override
    void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory) {
        reporterCategory.setWins(reporterCategory.getWins() + 1);
        opponentCategory.setLosses(opponentCategory.getLosses() + 1);
    }

    @Override
    void adjustUserUserCategoryStats(UserUserCategory userUserCategoryReporter, UserUserCategory userUserCategoryOpponent) {
        userUserCategoryReporter.setWins(userUserCategoryReporter.getWins() + 1);
        userUserCategoryOpponent.setLosses(userUserCategoryOpponent.getLosses() + 1);
    }

}
