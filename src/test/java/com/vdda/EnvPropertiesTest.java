package com.vdda;

import org.junit.Test;
import org.meanbean.test.BeanTester;

public class EnvPropertiesTest {

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(EnvProperties.class);
    }
}
