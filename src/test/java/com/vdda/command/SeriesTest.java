package com.vdda.command;

import com.vdda.command.service.SeriesService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SeriesTest {

    Series series;

    @Mocked
    SeriesService seriesService;

    @Before
    public void setUp() throws Exception {
        series = new Series(seriesService);
    }

    @Test
    public void runGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "series @user wld");

        Response response = series.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
            Map<String, String> parameters;
            List<String> args;
            seriesService.processRequest(parameters = withCapture(), args = withCapture());

            assertThat(parameters.get(SlackParameters.CHANNEL_ID.toString()), is("channelId"));
            assertThat(args.get(0), is("@user"));
            assertThat(args.get(1), is("wld"));
        }};
    }

    @Test
    public void incorrectArgCount() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "series @user");

        Response response = series.run(parameters);
        assertThat(response.getText(), containsString("Log the outcome of a series of contests."));
    }

    @Test
    public void incorrectOutcomeWrongChars() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "series @user RONG");

        Response response = series.run(parameters);
        assertThat(response.getText(), containsString("Log the outcome of a series of contests."));
    }

    @Test
    public void runGoldenAlternative() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "series @user WLD");

        Response response = series.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(series.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(series.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(series.getShortDescription(), is(notNullValue()));
    }


}