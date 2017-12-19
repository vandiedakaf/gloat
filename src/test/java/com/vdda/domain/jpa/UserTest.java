package com.vdda.domain.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UserTest {
    @Test
    public void construct() {
        User user = new User("teamId", "userId");

        assertThat(user.getTeamId(), is("teamId"));
        assertThat(user.getUserId(), is("userId"));
    }

    @Test
    public void meanBean() {
        new BeanTester().testBean(User.class);
    }

}
