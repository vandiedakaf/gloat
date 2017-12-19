package com.vdda.domain.repository;

import com.vdda.domain.jpa.UserCategory;
import com.vdda.domain.jpa.UserCategoryPK;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends CrudRepository<UserCategory, UserCategoryPK> {

	List<UserCategory> findAllByUserCategoryPK_CategoryIdOrderByEloDesc(Long categoryId);

	Optional<UserCategory> findUserCategoryByUserCategoryPK(UserCategoryPK userCategoryPK);

	List<UserCategory> findAllByUserCategoryPK_CategoryIdOrderByStreakCountDescStreakTypeDescModifiedDesc(Long categoryId);
}
