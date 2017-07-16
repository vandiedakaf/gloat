package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
public class TenderTest {

    private Tender tender;

    @Before
    public void setUp() throws Exception {
        tender = new Tender();
    }

    @Test
    public void runGolden() throws Exception {
        Response response = tender.run(null);

        assertThat(response.getText(), containsString("COMING SOON"));

    }

    @Test
    public void getCommand() throws Exception {
        assertThat(tender.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(tender.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(tender.getShortDescription(), is(notNullValue()));
    }
}
