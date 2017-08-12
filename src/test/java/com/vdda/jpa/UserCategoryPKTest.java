package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class UserCategoryPKTest {
    @Test
    public void construct() throws Exception {
        User user = new User();
        user.setId(1L);
        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 2L);

        assertThat(userCategoryPK.getUser().getId(), equalTo(1L));
        assertThat(userCategoryPK.getCategoryId(), equalTo(2L));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategoryPK.class);
    }

}
