package com.vdda.command;

import com.vdda.command.service.GloatService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
    public void runGolden() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");

        Response response = gloat.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            gloatService.processRequest(parameters = withCapture());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
        }};
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
