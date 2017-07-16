package com.vdda.command;

import com.vdda.command.service.TopService;
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

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
public class TopTest {

    Top top;

    @Mocked
    TopService topService;

    @Before
    public void setUp() throws Exception {
        top = new Top(topService);
    }

    @Test
    public void runGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "top");

        Response response = top.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            topService.processRequest(parameters = withCapture());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(equalTo("channelId")));
        }};
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(top.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(top.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(top.getShortDescription(), is(notNullValue()));
    }
}
