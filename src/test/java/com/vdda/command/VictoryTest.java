package com.vdda.command;

import com.vdda.command.service.VictoryService;
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

public class VictoryTest {

    Victory victory;

    @Mocked
    VictoryService victoryService;

    @Before
    public void setUp() throws Exception {
        victory = new Victory(victoryService);
    }

    @Test
    public void missingArgs() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "victory");
        Response response = victory.run(parameters);

        assertThat(response.getText(), containsString("Usage: `victory @user`"));
    }

    @Test
    public void runGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "victory user");

        Response response = victory.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            victoryService.processRequest(parameters = withCapture(), withNotNull());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(equalTo("channelId")));
        }};
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(victory.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(victory.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(victory.getShortDescription(), is(notNullValue()));
    }
}
