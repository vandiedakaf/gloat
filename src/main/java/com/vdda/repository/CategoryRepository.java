package com.vdda.repository;

import com.vdda.jpa.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {

    Category findByTeamIdAndChannelId(String teamId, String channelId);
}
