package com.vdda.domain.repository;

import com.vdda.domain.jpa.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByTeamIdAndChannelId(String teamId, String channelId);

    @Query(value = "SELECT (sum(wins) + sum(losses) + sum(draws)) / 2 FROM user_category WHERE category_id = ? GROUP BY category_id;", nativeQuery = true)
    int sumTotalPlayedByCategory(Long categoryId);

    @Query(value = "SELECT count(*) FROM user_category WHERE category_id = ? GROUP BY category_id;", nativeQuery = true)
    int longestCurrentSpreeWin(Long categoryId);

	@Query(value = "SELECT count(*) FROM user_category WHERE category_id = ? GROUP BY category_id;", nativeQuery = true)
	int longestCurrentSpreeLoss(Long categoryId);

}
