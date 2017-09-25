package com.vdda.repository;

import com.vdda.jpa.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByTeamIdAndChannelId(String teamId, String channelId);

    @Query(value = "SELECT sum(wins) + sum(losses) + sum(draws) FROM user_category WHERE category_id = ? GROUP BY category_id;", nativeQuery = true)
    int sumTotalPlayedByCategory(Long categoryId);

}
