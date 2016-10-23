package com.vdda.repository;

import com.vdda.jpa.Category;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by francois on 2016-10-23.
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {
}
