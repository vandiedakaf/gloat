package com.vdda.contest;

import com.vdda.EnvProperties;
import com.vdda.elo.EloCalculator;
import com.vdda.jpa.*;
import com.vdda.repository.ContestRepository;
import com.vdda.repository.UserCategoryRepository;
import com.vdda.repository.UserUserCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public abstract class ContestProcessor {

	private EnvProperties envProperties;
	private ContestRepository contestRepository;
	private UserCategoryRepository userCategoryRepository;
	private UserUserCategoryRepository userUserCategoryRepository;

	@Autowired
	public ContestProcessor(EnvProperties envProperties, ContestRepository contestRepository, UserCategoryRepository userCategoryRepository, UserUserCategoryRepository userUserCategoryRepository) {
		this.envProperties = envProperties;
		this.contestRepository = contestRepository;
		this.userCategoryRepository = userCategoryRepository;
		this.userUserCategoryRepository = userUserCategoryRepository;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void processContest(Contest contest) {

		Category category = contest.getCategory();

		UserCategoryPK reporterCategoryPK = new UserCategoryPK(contest.getReporter(), category.getId());
		UserCategory userCategoryReporter = getOrCreateUserCategory(reporterCategoryPK);

		UserCategoryPK opponentCategoryPK = new UserCategoryPK(contest.getOpponent(), category.getId());
		UserCategory userCategoryOpponent = getOrCreateUserCategory(opponentCategoryPK);

		EloCalculator eloCalculator = new EloCalculator(userCategoryReporter.getElo(), userCategoryOpponent.getElo(), Math.min(userCategoryReporter.getK(), userCategoryOpponent.getK()));

		EloCalculator.Ratings ratings = getRatings(eloCalculator);

		userCategoryReporter.setElo((int) Math.round(ratings.getReporterRating()));
		userCategoryReporter.setK(EloCalculator.determineK(userCategoryReporter.getWins() + userCategoryReporter.getLosses() + userCategoryReporter.getDraws()));
		userCategoryReporter.setStreakCount(contest.getContestOutcome().equals(userCategoryReporter.getStreakType()) ? userCategoryReporter.getStreakCount() + 1 : 1);
		userCategoryReporter.setStreakType(contest.getContestOutcome());

		userCategoryOpponent.setElo((int) Math.round(ratings.getOpponentRating()));
		userCategoryOpponent.setK(EloCalculator.determineK(userCategoryOpponent.getWins() + userCategoryOpponent.getLosses() + userCategoryOpponent.getDraws()));
		userCategoryOpponent.setStreakCount(contest.getContestOutcome().getOppositeEnum().equals(userCategoryOpponent.getStreakType()) ? userCategoryOpponent.getStreakCount() + 1 : 1);
		userCategoryOpponent.setStreakType(contest.getContestOutcome().getOppositeEnum());

		adjustUserCategoryStats(userCategoryReporter, userCategoryOpponent);

		userCategoryRepository.save(userCategoryReporter);
		userCategoryRepository.save(userCategoryOpponent);

		// wilson
		UserUserCategoryPK reporterUserUserCategoryPK = new UserUserCategoryPK(contest.getReporter(), contest.getOpponent(), category.getId());
		UserUserCategory userUserCategoryReporter = getOrCreateUserUserCategory(reporterUserUserCategoryPK);

		UserUserCategoryPK opponentUserUserCategoryPK = new UserUserCategoryPK(contest.getOpponent(), contest.getReporter(), category.getId());
		UserUserCategory userUserCategoryOpponent = getOrCreateUserUserCategory(opponentUserUserCategoryPK);

		adjustUserUserCategoryStats(userUserCategoryReporter, userUserCategoryOpponent);

		updateWilson(userUserCategoryReporter);
		updateWilson(userUserCategoryOpponent);

		userUserCategoryRepository.save(userUserCategoryReporter);
		userUserCategoryRepository.save(userUserCategoryOpponent);

		contest.setProcessed(true);
		contestRepository.save(contest);
	}

	private void updateWilson(UserUserCategory userUserCategory) {
		double wins = userUserCategory.getWins();
		double losses = userUserCategory.getLosses();

		double wilson = Math.sqrt((wins / (wins + losses)) + 1.6 * 1.6 / (2 * ((wins + losses) + wins)) - 1.6 * (
				((wins / (wins + losses)) * (1 - (wins / (wins + losses))) + 1.6 * 1.6 / (4 * ((wins + losses) + wins))) /
						((wins + losses) + wins))) / (1 + 1.6 * 1.6 / ((wins + losses) + wins));

		userUserCategory.setWilson(wilson);
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

	private UserUserCategory getOrCreateUserUserCategory(UserUserCategoryPK userUserCategoryPK) {
		UserUserCategory userUserCategory = userUserCategoryRepository.findOne(userUserCategoryPK);
		if (userUserCategory == null) {
			userUserCategory = new UserUserCategory(userUserCategoryPK);
			userUserCategory = userUserCategoryRepository.save(userUserCategory);
		}
		return userUserCategory;
	}

	abstract EloCalculator.Ratings getRatings(EloCalculator eloCalculator);

	abstract void adjustUserCategoryStats(UserCategory reporterCategory, UserCategory opponentCategory);

	abstract void adjustUserUserCategoryStats(UserUserCategory userUserCategoryReporter, UserUserCategory userUserCategoryOpponent);
}
