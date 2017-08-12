package com.vdda.callback;

import org.junit.Test;
import org.meanbean.test.BeanTester;


public class ActionTest {
    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Action.class);
    }
}
