package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-10
 * for vandiedakaf solutions
 */
public class OauthTest {
    @Test
    public void construct() throws Exception {
        Oauth oauth = new Oauth("teamId", "accessToken");

        assertThat(oauth.getTeamId(), equalTo("teamId"));
        assertThat(oauth.getAccessToken(), equalTo("accessToken"));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Oauth.class);
    }
}
