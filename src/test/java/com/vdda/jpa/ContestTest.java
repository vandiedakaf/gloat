package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContestTest {
    @Test
    public void construct() throws Exception {
        Category category = new Category("teamId", "channelId");
        User reporter = new User("teamId", "reporterId");
        User opponent = new User("teamId", "opponentId");

        Contest contest = new Contest(category, reporter, opponent, ContestOutcome.WIN);

        assertThat(contest.getCategory().getChannelId(), is("channelId"));
        assertThat(contest.getReporter().getUserId(), is("reporterId"));
        assertThat(contest.getOpponent().getUserId(), is("opponentId"));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Contest.class);
    }

}
