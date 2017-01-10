package com.vdda.repository;

import com.vdda.jpa.Oauth;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public interface OauthRepository extends CrudRepository<Oauth, String> {
}
