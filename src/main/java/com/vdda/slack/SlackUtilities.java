package com.vdda.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import com.vdda.domain.jpa.Oauth;
import com.vdda.domain.repository.OauthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SlackUtilities {
	private final Slack slack = Slack.getInstance();
	private final OauthRepository oauthRepository;

	@Autowired
	public SlackUtilities(OauthRepository oauthRepository) {
		this.oauthRepository = oauthRepository;
	}

	private Optional<UsersListResponse> usersList(String token) {

		UsersListResponse usersListResponse = new UsersListResponse();

		try {
			usersListResponse = slack.methods().usersList(
					UsersListRequest.builder()
							.token(token)
							.presence(1)
							.build());
		} catch (IOException | SlackApiException e) {
			log.debug("UsersListRequest", e);
			return Optional.empty();
		}

		if (!usersListResponse.isOk()) {
			log.error("Could not retrieve usersListResponse: {}", usersListResponse.toString());
			return Optional.empty();
		}

		return Optional.of(usersListResponse);
	}

	public Optional<User> getUserByUsername(String teamId, String userName) {

		Optional<List<User>> users = getUsers(teamId);
		if (!users.isPresent()) {
			return Optional.empty();
		}

		// remove potential '@' from userName
		String userNameSanitised = userName.replaceAll("@", "");

		return users.get().stream().filter(u -> u.getName().equals(userNameSanitised)).findFirst();
	}

	public Optional<User> getUserById(String teamId, String userId) {

		Optional<List<User>> users = getUsers(teamId);

		return users.flatMap(l -> l.stream().filter(u -> u.getId().equals(userId)).findFirst());
	}

	private Optional<List<User>> getUsers(String teamId) {

		Optional<String> token = getAccessToken(teamId);
		if (!token.isPresent()) {
			return Optional.empty();
		}

		Optional<UsersListResponse> usersListResponse = usersList(token.get());

		return usersListResponse.map(UsersListResponse::getMembers);
	}

	private Optional<String> getAccessToken(String teamId) {
		Oauth oauth = oauthRepository.findOne(teamId);
		if (oauth == null) {
			log.warn("Oauth token not found");
			return Optional.empty();
		}
		return Optional.of(oauth.getAccessToken());
	}

	public boolean sendChatMessage(String teamId, String channelId, String message) {

		Optional<String> token = getAccessToken(teamId);
		if (!token.isPresent()) {
			return false;
		}

		ChatPostMessageResponse chatPostMessageResponse;
		try {
			chatPostMessageResponse = slack.methods().chatPostMessage(
					ChatPostMessageRequest.builder()
							.token(token.get())
							.channel(channelId)
							.text(message)
							.build());
		} catch (IOException | SlackApiException e) {
			log.debug("ChatPostMessageRequest", e);
			return false;
		}

		if (!chatPostMessageResponse.isOk()) {
			log.error("Could not send ChatPostMessageRequest: {}", chatPostMessageResponse.toString());
			return false;
		}

		return true;
	}

}
