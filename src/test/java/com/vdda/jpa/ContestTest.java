package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ContestTest {
    @Test
    public void construct() throws Exception {
        Category category = new Category("teamId", "channelId");
        User reporter = new User("teamId", "reporterId");
        User opponent = new User("teamId", "opponentId");

        Contest contest = new Contest(category, reporter, opponent, ContestOutcome.WIN);

        assertThat(contest.getCategory().getChannelId(), equalTo("channelId"));
        assertThat(contest.getReporter().getUserId(), equalTo("reporterId"));
        assertThat(contest.getOpponent().getUserId(), equalTo("opponentId"));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Contest.class);
    }

}
