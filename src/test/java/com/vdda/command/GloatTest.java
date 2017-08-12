package com.vdda.command;

import com.vdda.command.service.GloatService;
import com.vdda.slack.Response;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GloatTest {

    private Gloat gloat;

    @Mocked
    GloatService gloatService;

    @Before
    public void setUp() throws Exception {
        gloat = new Gloat(gloatService);
    }

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
