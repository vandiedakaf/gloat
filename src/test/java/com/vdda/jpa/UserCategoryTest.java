package com.vdda.jpa;

import org.junit.Ignore;
import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-10
 * for vandiedakaf solutions
 */
@Ignore
public class UserCategoryTest {
    @Test
    public void construct() throws Exception {
        UserCategoryPK userCategoryPK = new UserCategoryPK(1L, 2L);
        UserCategory userCategory = new UserCategory(userCategoryPK, 1500L);

        assertThat(userCategory.getUserCategoryPK(), equalTo(userCategoryPK));

        assertThat(userCategory.getElo(), equalTo(1500L));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategory.class);
    }

}
