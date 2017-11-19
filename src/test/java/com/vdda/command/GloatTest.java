package com.vdda.command;

import com.vdda.command.service.GloatService;
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

public class GloatTest {

	@Tested
	private Gloat gloat;

	@Mocked
	private GloatService gloatService;

	@Before
	public void setUp() throws Exception {
		gloat = new Gloat(gloatService);
	}

	@Test
	public void runGolden() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId");

		Response response = gloat.run(request);
		assertThat(response.getText(), containsString("We're processing your request..."));

		new Verifications() {{
			Request requestVerify;
			gloatService.processRequest(requestVerify = withCapture());

			assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
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
	public void getUsageAdvanced() throws Exception {
		assertThat(gloat.getUsageAdvanced(), is(notNullValue()));
	}

	@Test
	public void getShortDescription() throws Exception {
		assertThat(gloat.getShortDescription(), is(notNullValue()));
	}
}
