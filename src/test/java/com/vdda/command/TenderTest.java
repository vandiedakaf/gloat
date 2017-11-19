package com.vdda.command;

import com.vdda.command.service.TenderService;
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

public class TenderTest {

	@Tested
	private Tender tender;

	@Mocked
	private TenderService tenderService;

	@Before
	public void setUp() throws Exception {
		tender = new Tender(tenderService);
	}

	@Test
	public void runGolden() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId");

		Response response = tender.run(request);
		assertThat(response.getText(), containsString("We're processing your request..."));

		new Verifications() {{
			Request requestVerify;
			tenderService.processRequest(requestVerify = withCapture());

			assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
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
	public void getUsageAdvanced() throws Exception {
		assertThat(tender.getUsageAdvanced(), is(notNullValue()));
	}

	@Test
	public void getShortDescription() throws Exception {
		assertThat(tender.getShortDescription(), is(notNullValue()));
	}
}
