package com.vdda.jpa;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois on 2016-10-23.
 */
public class UserTest {

    @Test
    public void construct() throws Exception {
        User user = new User(1234l);
        Long id = user.getId();

        assertThat(1234l, equalTo(id));
    }

}