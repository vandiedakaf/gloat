package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class UserTest {
    @Test
    public void construct() {
        User user = new User("teamId", "userId");

        assertThat(user.getTeamId(), equalTo("teamId"));
        assertThat(user.getUserId(), equalTo("userId"));
    }

    @Test
    public void meanBean() {
        new BeanTester().testBean(User.class);
    }

}
