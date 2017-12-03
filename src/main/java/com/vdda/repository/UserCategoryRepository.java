package com.vdda.repository;

import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends CrudRepository<UserCategory, UserCategoryPK> {

	List<UserCategory> findAllByUserCategoryPK_CategoryIdOrderByEloDesc(Long categoryId);

	Optional<UserCategory> findUserCategoryByUserCategoryPK(UserCategoryPK userCategoryPK);

	List<UserCategory> findAllByUserCategoryPK_CategoryIdOrderByStreakCountDescStreakTypeDesc(Long categoryId);
}
