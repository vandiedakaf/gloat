package com.vdda.repository;

import com.vdda.jpa.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByTeamIdAndChannelId(String teamId, String channelId);
}
