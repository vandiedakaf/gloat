package com.vdda.repository;

import com.vdda.jpa.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Category findByTeamIdAndChannelId(String teamId, String channelId);
}
