package com.vdda.jpa;

import org.junit.Test;
import org.meanbean.test.BeanTester;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
public class CategoryTest {
    @Test
    public void construct() throws Exception {
        Category category = new Category("team_id", "channel_id");

        assertThat(category.getTeamId(), equalTo("team_id"));
        assertThat(category.getChannelId(), equalTo("channel_id"));
    }

    @Test
    public void meanBean() throws Exception {
        //new BeanTester().testBean(Category.class);
    }

}
