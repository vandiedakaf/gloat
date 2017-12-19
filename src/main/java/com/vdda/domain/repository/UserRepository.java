package com.vdda.domain.repository;

import com.vdda.domain.jpa.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByTeamIdAndUserId(String teamId, String userId);
}
