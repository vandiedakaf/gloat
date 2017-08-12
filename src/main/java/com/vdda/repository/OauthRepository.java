package com.vdda.repository;

import com.vdda.jpa.Oauth;
import org.springframework.data.repository.CrudRepository;

public interface OauthRepository extends CrudRepository<Oauth, String> {
}
