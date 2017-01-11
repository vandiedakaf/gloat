package com.vdda.slack;

import org.junit.Test;
import org.meanbean.test.BeanTester;

/**
 * Created by francois
 * on 2016-12-27
 * for vandiedakaf solutions
 */
public class AttachmentTest {

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Attachment.class);
    }
}
