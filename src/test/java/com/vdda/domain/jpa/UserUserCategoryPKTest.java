package com.vdda.domain.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UserUserCategoryPKTest {
    @Test
    public void construct() throws Exception {
        User user = new User();
        user.setId(1L);

		User opponent = new User();
		opponent.setId(2L);

        UserUserCategoryPK userUserCategoryPK = new UserUserCategoryPK(user, opponent, 3L);

        assertThat(userUserCategoryPK.getUser().getId(), is(1L));
		assertThat(userUserCategoryPK.getOpponent().getId(), is(2L));
        assertThat(userUserCategoryPK.getCategoryId(), is(3L));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(UserUserCategoryPK.class);
    }

}
