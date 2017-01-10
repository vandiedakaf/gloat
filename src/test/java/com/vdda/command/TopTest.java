package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
public class TopTest {
    @Test
    public void runGolden() throws Exception {
        Top top = new Top();
        Response response = top.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }
}
