package com.vdda.repository;

import com.vdda.jpa.UserCategory;
import com.vdda.jpa.UserCategoryPK;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public interface UserCategoryRepository extends CrudRepository<UserCategory, UserCategoryPK> {
}
