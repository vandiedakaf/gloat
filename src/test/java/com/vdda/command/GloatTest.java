package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
public class GloatTest {

    private Gloat gloat;

    @Before
    public void setUp() throws Exception {
        gloat = new Gloat();
    }

    @Test
    public void runGolden() throws Exception {
        Response response = gloat.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }

    @Test
    public void getCommand() throws Exception {
        assertThat(gloat.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(gloat.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(gloat.getShortDescription(), is(notNullValue()));
    }
}
