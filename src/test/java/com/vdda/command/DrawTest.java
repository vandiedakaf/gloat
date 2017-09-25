package com.vdda.command;

import com.vdda.command.service.DrawService;
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

public class DrawTest {

    private Draw draw;

    @Mocked
    DrawService drawService;

    @Before
    public void setUp() throws Exception {
        draw = new Draw(drawService);
    }

    @Test
    public void missingArgs() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "draw");
        Response response = draw.run(parameters);

        assertThat(response.getText(), containsString("Usage: `draw @user`"));
    }

    @Test
    public void drawGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "draw user");

        Response response = draw.run(parameters);


        new Verifications() {{
            Map<String, String> parameters;
            drawService.processRequest(parameters = withCapture(), withNotNull());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
        }};
        assertThat(response.getText(), containsString("We're processing your request..."));
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(draw.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(draw.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(draw.getShortDescription(), is(notNullValue()));
    }
}
