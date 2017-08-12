package com.vdda.slack;

import org.junit.Test;
import org.meanbean.test.BeanTester;

public class AttachmentTest {

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Attachment.class);
    }
}
