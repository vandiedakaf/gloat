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
public class TenderTest {
    @Test
    public void runGolden() throws Exception {
        Tender tender = new Tender();
        Response response = tender.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }
}
