package com.vdda.command;

import com.vdda.command.service.SeriesService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Request;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SeriesTest {

    @Tested
    private Series series;

	@Mocked
	private SeriesService seriesService;

    @Before
    public void setUp() throws Exception {
        series = new Series(seriesService);
    }

    @Test
    public void runGolden() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=@user wld");

        Response response = series.run(request);
        assertThat(response.getText(), containsString("We're processing your request..."));

        new Verifications() {{
			Request requestVerify;
            seriesService.processRequest(requestVerify = withCapture());

            assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is("channelId"));
            assertThat(requestVerify.getArguments().get(0), is("@user"));
            assertThat(requestVerify.getArguments().get(1), is("wld"));
        }};
    }

    @Test
    public void incorrectArgCount() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=series @user");

        Response response = series.run(request);
        assertThat(response.getText(), containsString("Log the outcome of contests."));
    }

    @Test
    public void incorrectOutcomeWrongChars() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=series @user RONG");

        Response response = series.run(request);
        assertThat(response.getText(), containsString("Log the outcome of contests."));
    }

    @Test
    public void runGoldenAlternative() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=user wld");

        Response response = series.run(request);
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