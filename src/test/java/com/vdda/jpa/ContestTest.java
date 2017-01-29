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
        Category category = new Category("teamId", "channelId");
        User winner = new User("teamId", "winnerId");
        User loser = new User("teamId", "loserId");

        Contest contest = new Contest(category, winner, loser);

        assertThat(contest.getCategory().getChannelId(), equalTo("channelId"));
        assertThat(contest.getWinner().getUserId(), equalTo("winnerId"));
        assertThat(contest.getLoser().getUserId(), equalTo("loserId"));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Contest.class);
    }

}
