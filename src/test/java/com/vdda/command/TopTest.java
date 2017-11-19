package com.vdda.command;

import com.vdda.command.service.TopService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Request;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TopTest {

	@Tested
	private Top top;

	@Mocked
	private TopService topService;

	@Before
	public void setUp() throws Exception {
		top = new Top(topService);
	}

	@Test
	public void runGolden() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=top");

		Response response = top.run(request);
		assertThat(response.getText(), containsString("We're processing your request..."));

		new Verifications() {{
			Request requestVerify;
			topService.processRequest(requestVerify = withCapture());

			assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
		}};
	}

	@Test
	public void runGoldenWithExtraArg() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=top 5");

		Response response = top.run(request);
		assertThat(response.getText(), containsString("We're processing your request..."));

		new Verifications() {{
			Request requestVerify;
			topService.processRequest(requestVerify = withCapture(), 5);

			assertThat(requestVerify.getParameter(SlackParameters.CHANNEL_ID.toString()), is(("channelId")));
		}};
	}

	@Test
	public void runWithIncorrectArg() throws Exception {

		Request request = new Request(SlackParameters.CHANNEL_ID.toString() + "=channelId&" + SlackParameters.TEAM_ID.toString() + "=teamId&" + SlackParameters.TEXT.toString() + "=top -5");

		Response response = top.run(request);
		assertThat(response.getText(), containsString("Usage"));
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

	@Test
	public void validateArgsGolden() throws Exception {
		List<String> args = Collections.singletonList("123");
		assertThat(top.validArgs(args), is((true)));
	}

	@Test
	public void validateArgsNotAnInt() throws Exception {
		List<String> args = Collections.singletonList("notAnInt");
		assertThat(top.validArgs(args), is((false)));
	}

	@Test
	public void validateArgsNegative() throws Exception {
		List<String> args = Collections.singletonList("-123");
		assertThat(top.validArgs(args), is((false)));
	}

	@Test
	public void validateArgsDecimal() throws Exception {
		List<String> args = Collections.singletonList("-123.321");
		assertThat(top.validArgs(args), is((false)));
	}

	@Test
	public void validateArgsZero() throws Exception {
		List<String> args = Collections.singletonList("0");
		assertThat(top.validArgs(args), is((false)));
	}
}
