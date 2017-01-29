package com.vdda.repository;

import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public interface UserCategoryRepository extends CrudRepository<UserCategory, UserCategoryPK> {

    @Query(value = "SELECT * FROM user_category WHERE category_id = ? ORDER BY elo DESC", nativeQuery = true)
    List<UserCategory> findAllByCategoryIdOrderByEloDesc(Long categoryId);
}
