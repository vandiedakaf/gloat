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
public class GloatTest {
    @Test
    public void runGolden() throws Exception {
        Gloat gloat = new Gloat();
        Response response = gloat.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }
}
