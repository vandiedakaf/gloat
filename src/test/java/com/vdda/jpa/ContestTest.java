package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-10
 * for vandiedakaf solutions
 */
public class ContestTest {
    @Test
    public void construct() throws Exception {
        Contest contest = new Contest(1L, 2L, 3L);

        assertThat(contest.getCategoryId(), equalTo(1L));
        assertThat(contest.getWinnerId(), equalTo(2L));
        assertThat(contest.getLoserId(), equalTo(3L));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Contest.class);
    }

}
