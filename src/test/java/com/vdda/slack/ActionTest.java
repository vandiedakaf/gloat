package com.vdda.slack;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2016-12-29
 * for vandiedakaf solutions
 */
public class ActionTest {

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Action.class);
    }

    @Test
    public void getType() throws Exception {

        assertThat(new Action().getType(), is(equalTo("button")));

    }
}
