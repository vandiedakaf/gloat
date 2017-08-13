package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.UserCategory;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrawProcessor extends ContestProcessor {

    @Autowired
    public DrawProcessor(EnvProperties envProperties, ContestRepository contestRepository, UserCategoryRepository userCategoryRepository) {
        super(envProperties, contestRepository, userCategoryRepository);
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

}
