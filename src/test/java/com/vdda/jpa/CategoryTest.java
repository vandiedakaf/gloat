package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CategoryTest {
    @Test
    public void construct() throws Exception {
        Category category = new Category("team_id", "channel_id");

        assertThat(category.getTeamId(), is("team_id"));
        assertThat(category.getChannelId(), is("channel_id"));
    }

    @Test
    public void meanBean() throws Exception {
        new BeanTester().testBean(Category.class);
    }

}
