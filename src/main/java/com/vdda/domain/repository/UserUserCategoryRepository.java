package com.vdda.domain.repository;

import com.vdda.domain.jpa.UserUserCategory;
import com.vdda.domain.jpa.UserUserCategoryPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserUserCategoryRepository extends CrudRepository<UserUserCategory, UserUserCategoryPK> {

	@Query(value = "SELECT * FROM user_user_category WHERE user_id = ? AND category_id = ?  ORDER BY wilson DESC", nativeQuery = true)
	List<UserUserCategory> findWilsonMax(Long userId, Long categoryId);

	@Query(value = "SELECT * FROM user_user_category WHERE user_id = ? AND category_id = ?  ORDER BY wilson ASC", nativeQuery = true)
	List<UserUserCategory> findWilsonMin(Long userId, Long categoryId);

}
