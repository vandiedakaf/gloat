package com.vdda.jpa;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by francois on 2016-10-23.
 */
public class CategoryTest {
    @Test
    public void construct() throws Exception {
        Category category = new Category(1234l);
        Long id = category.getId();

        assertThat(1234l, equalTo(id));
    }

}
