package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois on 2016-10-23.
 */
public class UserTest {

    @Test
    public void construct() {
        User user = new User(1234l);
        Long id = user.getId();

        assertThat(1234l, equalTo(id));
    }

    @Test
    public void meanBean() {
        new BeanTester().testBean(User.class);
    }

}
