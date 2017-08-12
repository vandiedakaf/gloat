package com.vdda.repository;

import com.vdda.jpa.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByTeamIdAndUserId(String teamId, String userId);
}
