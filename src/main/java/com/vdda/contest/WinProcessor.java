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
public class WinProcessor extends ContestProcessor {

    @Autowired
    public WinProcessor(EnvProperties envProperties, ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
        super(envProperties, contestRepository, userCategoryRepository, userUserCategoryRepository);
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
