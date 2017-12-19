package com.vdda.callback;

import com.vdda.command.service.CallbackBuilder;
import com.vdda.contest.ContestResolver;
import com.vdda.domain.jpa.Category;
import com.vdda.domain.jpa.UserCategoryPK;
import com.vdda.domain.repository.CategoryRepository;
import com.vdda.domain.repository.UserCategoryRepository;
import com.vdda.domain.repository.UserRepository;
import com.vdda.slack.Attachment;
import com.vdda.slack.Response;
import com.vdda.slack.SlackUtilities;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vdda.command.service.CallbackBuilder.callbackIdBuilder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmContestTest {

	private static final String CHANNEL_ID = "channelId";
	private static final String TEAM_ID = "teamId";
	private static final String USER_ID = "userId";

	@Autowired
	private ConfirmContest confirmContest;

	@Mocked
	private CategoryRepository categoryRepository;
	@Mocked
	private UserRepository userRepository;
	@Mocked
	private ContestResolver contestResolver;
	@Mocked
	private UserCategoryRepository userCategoryRepository;
	@Mocked
	private SlackUtilities slackUtilities;

	@Before
	public void setUp() throws Exception {
		confirmContest = new ConfirmContest(categoryRepository, userRepository, contestResolver, userCategoryRepository, slackUtilities) {
			@Override
			protected Attachment confirmAttachment(String opponentId) {
				Attachment attachment = new Attachment();
				attachment.setText("testConfirm");
				return attachment;
			}

			@Override
			protected Attachment denyAttachment(String opponentId) {
				Attachment attachment = new Attachment();
				attachment.setText("testDeny");
				return attachment;
			}

			@Override
			protected void persistContests(CallbackRequest callbackRequest, Category category, com.vdda.domain.jpa.User reporter, com.vdda.domain.jpa.User opponent) {

			}

			@Override
			protected void notifyChannelBefore() {
				getOrCreateUserCategory(null);
			}

			@Override
			public String getCallbackId() {
				return "callbackId";
			}
		};
	}

	@Test
	public void getCallbackId() throws Exception {
		assertThat(confirmContest.getCallbackId(), is(("callbackId")));
	}

	@Test
	public void runGoldenNo() throws Exception {
		CallbackRequest callbackRequest = new CallbackRequest();
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setName("no");
		actions.add(action);
		callbackRequest.setActions(actions);
		callbackRequest.setCallbackId(callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID));

		Response response = confirmContest.run(callbackRequest);
		assertThat(response.getAttachments().get(0).getText(), containsString("testDeny"));
	}

	@Test
	public void runGoldenYes() throws Exception {

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategory();

			userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
			result = mockUser();
		}};

		CallbackRequest callbackRequest = new CallbackRequest();
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setName("yes");
		actions.add(action);
		callbackRequest.setActions(actions);
		callbackRequest.setCallbackId(callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID));

		User user = new User();
		user.setId(USER_ID);
		callbackRequest.setUser(user);

		Channel channel = new Channel();
		channel.setId(CHANNEL_ID);
		callbackRequest.setChannel(channel);

		Team team = new Team();
		team.setId(TEAM_ID);
		callbackRequest.setTeam(team);

		Response response = confirmContest.run(callbackRequest);
		assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));
	}

	@Test
	public void runGoldenYesNewUser() throws Exception {

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategory();
		}};

		CallbackRequest callbackRequest = new CallbackRequest();
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setName("yes");
		actions.add(action);
		callbackRequest.setActions(actions);
		callbackRequest.setCallbackId(callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID));

		User user = new User();
		user.setId(USER_ID);
		callbackRequest.setUser(user);

		Channel channel = new Channel();
		channel.setId(CHANNEL_ID);
		callbackRequest.setChannel(channel);

		Team team = new Team();
		team.setId(TEAM_ID);
		callbackRequest.setTeam(team);

		Response response = confirmContest.run(callbackRequest);
		assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

		new Verifications() {{
			com.vdda.domain.jpa.User userSaved;
			userRepository.save(userSaved = withCapture());
			assertThat(userSaved.getUserId(), is((USER_ID)));
		}};
	}

	@Test
	public void runGoldenYesNewCategory() throws Exception {

		new Expectations() {{
			userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
			result = mockUser();
		}};

		CallbackRequest callbackRequest = new CallbackRequest();
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setName("yes");
		actions.add(action);
		callbackRequest.setActions(actions);
		callbackRequest.setCallbackId(CallbackBuilder.callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID));

		User user = new User();
		user.setId(USER_ID);
		callbackRequest.setUser(user);

		Channel channel = new Channel();
		channel.setId(CHANNEL_ID);
		callbackRequest.setChannel(channel);

		Team team = new Team();
		team.setId(TEAM_ID);
		callbackRequest.setTeam(team);

		Response response = confirmContest.run(callbackRequest);
		assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));

		new Verifications() {{
			Category category;
			categoryRepository.save(category = withCapture());
			assertThat(category.getChannelId(), is((CHANNEL_ID)));
		}};
	}

	@Test
	public void runGoldenYesNewUserCategory() throws Exception {

		new Expectations() {{
			categoryRepository.findByTeamIdAndChannelId(TEAM_ID, CHANNEL_ID);
			result = mockCategory();

			userRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID);
			result = mockUser();

			userCategoryRepository.findOne((UserCategoryPK) any);
			result = null;
		}};

		CallbackRequest callbackRequest = new CallbackRequest();
		List<Action> actions = new ArrayList<>();
		Action action = new Action();
		action.setName("yes");
		actions.add(action);
		callbackRequest.setActions(actions);
		callbackRequest.setCallbackId(callbackIdBuilder(Callbacks.CONFIRM_SERIES.toString(), USER_ID));

		User user = new User();
		user.setId(USER_ID);
		callbackRequest.setUser(user);

		Channel channel = new Channel();
		channel.setId(CHANNEL_ID);
		callbackRequest.setChannel(channel);

		Team team = new Team();
		team.setId(TEAM_ID);
		callbackRequest.setTeam(team);

		Response response = confirmContest.run(callbackRequest);
		assertThat(response.getAttachments().get(0).getText(), containsString("testConfirm"));
	}

	private Optional<Category> mockCategory() {
		Category category = new Category(TEAM_ID, CHANNEL_ID);
		category.setId(1L);
		return Optional.of(category);
	}

	private Optional<com.vdda.domain.jpa.User> mockUser() {
		com.vdda.domain.jpa.User user = new com.vdda.domain.jpa.User(TEAM_ID, USER_ID);
		user.setId(1L);
		return Optional.of(user);
	}

}
