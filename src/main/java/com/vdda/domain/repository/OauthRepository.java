package com.vdda.domain.repository;

import com.vdda.domain.jpa.Oauth;
import org.springframework.data.repository.CrudRepository;

public interface OauthRepository extends CrudRepository<Oauth, String> {
}
