package com.vdda.action;

import org.junit.Ignore;
import org.junit.Test;
import org.meanbean.test.BeanTester;


/**
 * Created by francois
 * on 2017-01-03
 * for vandiedakaf solutions
 */
@Ignore
public class ActionTest {
    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Action.class);
    }
}
