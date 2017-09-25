package com.vdda.slack;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ActionTest {

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Action.class);
    }

    @Test
    public void getType() throws Exception {

        assertThat(new Action().getType(), is(("button")));

    }
}
