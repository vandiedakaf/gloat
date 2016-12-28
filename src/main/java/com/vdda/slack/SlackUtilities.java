package com.vdda.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by francois
 * on 2016-12-28
 * for vandiedakaf solutions
 */
@Slf4j
public class SlackUtilities {
    final Slack slack = Slack.getInstance();
    final String teamId;
    final String token;

    public SlackUtilities(String teamId) {
        this.teamId = teamId;
        // TODO get token from teamId
        this.token = System.getenv("SLACK_TOKEN");
    }

    public UsersListResponse usersList() {

        UsersListResponse usersListResponse = new UsersListResponse();

        try {
            usersListResponse = slack.methods().usersList(
                    UsersListRequest.builder()
                            .token(token)
                            .presence(1)
                            .build());
        } catch (IOException | SlackApiException e) {
            log.debug("UsersListRequest", e);
        }

        return usersListResponse;
    }

    public Optional<User> getUser(String userName) {
        // remove '@'
        String userNameSanitised = userName.replaceAll("@","");

        UsersListResponse usersListResponse = usersList();

        List<User> users = usersListResponse.getMembers();

        Optional<User> user = users.stream().filter(u -> u.getName().equals(userNameSanitised)).findFirst();

        return user;
    }

}
