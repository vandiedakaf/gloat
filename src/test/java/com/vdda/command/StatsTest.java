package com.vdda.command;

import com.vdda.command.service.StatsService;
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

public class StatsTest {

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

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=stats");

		Response response = stats.run(request);
		assertThat(response.getText(), containsString("We're processing your request..."));

		new Verifications() {{
			Request requestVerify;
			//noinspection unchecked
			statsService.processRequest(requestVerify = withCapture());

			assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
		}};
	}

	@Test
	public void getCommand() throws Exception {
		assertThat(stats.getCommand(), is(notNullValue()));
	}

	@Test
	public void getUsage() throws Exception {
		assertThat(stats.getUsage(), is(notNullValue()));
	}

	@Test
	public void getUsageAdvanced() throws Exception {
		assertThat(stats.getUsageAdvanced(), is(notNullValue()));
	}

	@Test
	public void getShortDescription() throws Exception {
		assertThat(stats.getShortDescription(), is(notNullValue()));
	}

}