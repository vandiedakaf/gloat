package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-10
 * for vandiedakaf solutions
 */
public class UserCategoryTest {
    @Test
    public void construct() throws Exception {
        User user = new User();
        user.setId(1L);

        UserCategoryPK userCategoryPK = new UserCategoryPK(user, 2L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 1500);

        assertThat(userCategory.getUserCategoryPK(), equalTo(userCategoryPK));

        assertThat(userCategory.getElo(), equalTo(1500));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategory.class);
    }

}
