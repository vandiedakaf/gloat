package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GloatTest {

    @Autowired
    private Gloat gloat;

    @Test
    @Ignore
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
