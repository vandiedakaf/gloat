package com.vdda.jpa;

import org.junit.Ignore;
import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-10
 * for vandiedakaf solutions
 */
@Ignore
public class UserCategoryPKTest {
    @Test
    public void construct() throws Exception {
        UserCategoryPK userCategoryPK = new UserCategoryPK(1L, 2L);

        assertThat(userCategoryPK.getUserId(), equalTo(1L));
        assertThat(userCategoryPK.getCategoryId(), equalTo(2L));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategoryPK.class);
    }

}
