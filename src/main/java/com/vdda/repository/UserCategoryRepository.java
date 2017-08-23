package com.vdda.repository;

import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends CrudRepository<UserCategory, UserCategoryPK> {

    // TODO replace with List<UserCategory> findAllByUserCategoryPK_CategoryIdOrderByEloDesc(Long categoryId);
    // TODO remove calibrationGames constraint here and add it as filter after the fact
    @Query(value = "SELECT * FROM user_category WHERE category_id = ? AND (wins + losses + draws) >= ? ORDER BY elo DESC", nativeQuery = true)
    List<UserCategory> findAllByCategoryIdOrderByEloDesc(Long categoryId, Integer calibrationGames);

    Optional<UserCategory> findUserCategoryByUserCategoryPK(UserCategoryPK userCategoryPK);
}
