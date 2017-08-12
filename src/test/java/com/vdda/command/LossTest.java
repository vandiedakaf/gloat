package com.vdda.command;

import com.vdda.command.service.LossService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class LossTest {

    private Loss loss;

    @Mocked
    LossService lossService;

    @Before
    public void setUp() throws Exception {
        loss = new Loss(lossService);
    }

    @Test
    public void missingArgs() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "loss");
        Response response = loss.run(parameters);

        assertThat(response.getText(), containsString("Usage: `loss @user`"));
    }

    @Test
    public void runGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "loss user");

        Response response = loss.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            lossService.processRequest(parameters = withCapture(), withNotNull());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(equalTo("channelId")));
        }};
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(loss.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(loss.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(loss.getShortDescription(), is(notNullValue()));
    }
}
