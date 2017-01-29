package com.vdda.elo;

import com.vdda.jpa.*;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by francois
 * on 2017-01-14
 * for vandiedakaf solutions
 */
@Service
@Slf4j
public class EloService {

    private static final Integer ELO_INIT = 1500;

    private ContestRepository contestRepository;
    private UserCategoryRepository userCategoryRepository;
    private SlackUtilities slackUtilities;

    @Autowired
    public EloService(ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, SlackUtilities slackUtilities) {
        this.contestRepository = contestRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.slackUtilities = slackUtilities;
    }

    /**
     * At the moment we're just processing any and all contests. TODO: This can be limited to specific categories.
     */
    @Async
    public Future<?> processContests() {
        List<Contest> contests = contestRepository.findByProcessedOrderByCreatedAsc(false);

        contests.forEach(this::processContest);

        return new AsyncResult<>(null);
    }

    @Transactional
    private void processContest(Contest contest) {

        User winner = contest.getWinner();
        User loser = contest.getLoser();
        Category category = contest.getCategory();

        UserCategoryPK winnerCategoryPK = new UserCategoryPK(winner, category.getId());
        UserCategory winnerCategory = userCategoryRepository.findOne(winnerCategoryPK);
        if (winnerCategory == null) {
            winnerCategory = new UserCategory(new UserCategoryPK(winner, category.getId()), ELO_INIT);
            userCategoryRepository.save(winnerCategory);
        }

        UserCategoryPK loserCategoryPK = new UserCategoryPK(loser, category.getId());
        UserCategory loserCategory = userCategoryRepository.findOne(loserCategoryPK);
        if (loserCategory == null) {
            loserCategory = new UserCategory(new UserCategoryPK(loser, category.getId()), ELO_INIT);
            userCategoryRepository.save(loserCategory);
        }

        notifyChannelBefore(contest);

        EloCalculator eloCalculator = new EloCalculator(winnerCategory.getElo(), loserCategory.getElo(), Math.min(winnerCategory.getK(), loserCategory.getK()));

        EloCalculator.Ratings ratings = eloCalculator.adjustedRating(EloCalculator.Outcome.WIN);

        winnerCategory.setElo((int) Math.round(ratings.getPlayerRating()));
        winnerCategory.setContestTotal(winnerCategory.getContestTotal() + 1); // race condition
        userCategoryRepository.save(winnerCategory);

        loserCategory.setElo((int) Math.round(ratings.getOpponentRating()));
        loserCategory.setContestTotal(loserCategory.getContestTotal() + 1); // race condition
        userCategoryRepository.save(loserCategory);

        contest.setProcessed(true);
        contestRepository.save(contest);

        notifyChannelAfter(contest);
    }

    private void notifyChannelBefore(Contest contest) {

        UserCategoryPK winnerCategoryPK = new UserCategoryPK(contest.getWinner(), contest.getCategory().getId());
        UserCategory userCategoryWinner = userCategoryRepository.findOne(winnerCategoryPK);

        UserCategoryPK loserCategoryPK = new UserCategoryPK(contest.getLoser(), contest.getCategory().getId());
        UserCategory userCategoryLoser = userCategoryRepository.findOne(loserCategoryPK);

        String message = "<@" + contest.getWinner().getUserId() + "> (" + userCategoryWinner.getElo() + ") is gloating about their victory over <@" + contest.getLoser().getUserId() + "> (" + userCategoryLoser.getElo() + ").";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

    private void notifyChannelAfter(Contest contest) {

        UserCategoryPK winnerCategoryPK = new UserCategoryPK(contest.getWinner(), contest.getCategory().getId());
        UserCategory userCategoryWinner = userCategoryRepository.findOne(winnerCategoryPK);

        UserCategoryPK loserCategoryPK = new UserCategoryPK(contest.getLoser(), contest.getCategory().getId());
        UserCategory userCategoryLoser = userCategoryRepository.findOne(loserCategoryPK);

        String message = "\nTheir new ratings are <@" + contest.getWinner().getUserId() + "> (" + userCategoryWinner.getElo() + ") and <@" + contest.getLoser().getUserId() + "> (" + userCategoryLoser.getElo() + ").";

        slackUtilities.sendChatMessage(contest.getCategory().getTeamId(), contest.getCategory().getChannelId(), message);
    }

}
