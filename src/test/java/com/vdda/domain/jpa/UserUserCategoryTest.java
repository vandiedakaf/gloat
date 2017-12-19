package com.vdda.domain.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class UserUserCategoryTest {
    @Test
    public void construct() throws Exception {
        User user = new User();
        user.setId(1L);

        User opponent = new User();
        opponent.setId(2L);

        UserUserCategoryPK userUserCategoryPK = new UserUserCategoryPK(user, opponent, 3L);
        UserUserCategory userUserCategory = new UserUserCategory(userUserCategoryPK);

        assertThat(userUserCategory.getUserUserCategoryPK(), equalTo(userUserCategoryPK));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserCategory.class);
    }

}
