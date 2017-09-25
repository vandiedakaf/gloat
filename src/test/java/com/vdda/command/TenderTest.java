package com.vdda.command;

import com.vdda.command.service.TenderService;
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

public class TenderTest {

    private Tender tender;

    @Mocked
    TenderService tenderService;

    @Before
    public void setUp() throws Exception {
        tender = new Tender(tenderService);
    }

    @Test
    public void runGolden() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");

        Response response = tender.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            tenderService.processRequest(parameters = withCapture());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
        }};
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
