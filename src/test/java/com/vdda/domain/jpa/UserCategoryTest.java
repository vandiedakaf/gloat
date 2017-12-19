package com.vdda.domain.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class UserCategoryTest {
    @Test
    public void construct() throws Exception {
        User user = new User();
        user.setId(1L);

        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 2L);
        UserCategory userCategory = new UserCategory(userCategoryPK);

        assertThat(userCategory.getUserCategoryPK(), equalTo(userCategoryPK));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategory.class);
    }

}
