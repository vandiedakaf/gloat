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
public class DrawTest {

    @Test
    public void runGolden() throws Exception {
        Draw draw = new Draw();
        Response response = draw.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }
}
