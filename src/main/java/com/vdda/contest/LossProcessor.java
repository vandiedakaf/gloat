package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserUserCategory;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserUserCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LossProcessor extends ContestProcessor {

    @Autowired
    public LossProcessor(EnvProperties envProperties, ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
        super(envProperties, contestRepository, userCategoryRepository, userUserCategoryRepository);
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
