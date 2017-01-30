package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
public class DrawTest {

    private Draw draw;

    @Before
    public void setUp() throws Exception {
        draw = new Draw();
    }

    @Test
    public void runGolden() throws Exception {
        Response response = draw.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(draw.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(draw.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(draw.getShortDescription(), is(notNullValue()));
    }
}
