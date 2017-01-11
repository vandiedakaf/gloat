package com.vdda.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.Oauth;
import com.vdda.repository.OauthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by francois
 * on 2016-12-28
 * for vandiedakaf solutions
 */
@Slf4j
@Service
public class SlackUtilities {
    private final Slack slack = Slack.getInstance();
    private String token;

    private OauthRepository oauthRepository;

    @Autowired
    public SlackUtilities(OauthRepository oauthRepository) {
        this.oauthRepository = oauthRepository;
    }

    private UsersListResponse usersList() {

        // TODO check if initialised

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

        if (!usersListResponse.isOk()) {
            log.error("Could not retrieve usersListResponse: {}", usersListResponse.toString());
        }

        return usersListResponse;
    }

    public Optional<User> getUser(String teamId, String userName) {
        log.debug("SlackUtilities.getUser");

        Oauth oauth = oauthRepository.findOne(teamId);
        if (oauth == null) {
            log.warn("Oauth token not found");
            return Optional.empty();
        }
        this.token = oauth.getAccessToken();

        // remove '@'
        String userNameSanitised = userName.replaceAll("@", "");

        UsersListResponse usersListResponse = usersList();

        List<User> users = usersListResponse.getMembers();

        if (users == null) {
            return Optional.empty();
        }

        return users.stream().filter(u -> u.getName().equals(userNameSanitised)).findFirst();
    }

}