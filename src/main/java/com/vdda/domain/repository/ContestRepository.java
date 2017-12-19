package com.vdda.domain.repository;

import com.vdda.domain.jpa.Contest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContestRepository extends CrudRepository<Contest, Long> {

    List<Contest> findByProcessedOrderByCreatedAsc(Boolean processed);
}
