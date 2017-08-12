package com.vdda.callback;

import org.junit.Test;
import org.meanbean.test.BeanTester;

public class CallbackRequestTest {
    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(CallbackRequest.class);
    }
}
