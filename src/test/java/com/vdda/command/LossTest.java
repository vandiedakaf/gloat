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
public class LossTest {
    @Test
    public void runGolden() throws Exception {
        Loss loss = new Loss();
        Response response = loss.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }
}
