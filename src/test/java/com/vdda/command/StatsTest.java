package com.vdda.command;

import com.vdda.command.service.StatsService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StatsTest {
    private static final String TEAM_ID = "teamId";
    private static final String CHANNEL_ID = "channelId";

    @Tested
    private Stats stats;

    @Mocked
    private StatsService statsService;

    @Before
    public void setUp() throws Exception {
        stats = new Stats(statsService);
    }

    @Test
    public void runGoldenSelf() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), CHANNEL_ID);
        parameters.put(SlackParameters.TEAM_ID.toString(), TEAM_ID);
        parameters.put(SlackParameters.TEXT.toString(), "stats");

        Response response = stats.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            //noinspection unchecked
            statsService.processRequest(parameters = withCapture(), (List<String>) any);

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is(equalTo("channelId")));
        }};
    }

}