package com.vdda.repository;

import com.vdda.jpa.Contest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public interface ContestRepository extends CrudRepository<Contest, Long> {

    List<Contest> findByProcessedOrderByCreatedAsc(Boolean processed);
}
