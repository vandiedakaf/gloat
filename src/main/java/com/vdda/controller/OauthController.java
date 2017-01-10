package com.vdda.controller;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.oauth.OAuthAccessRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersIdentityRequest;
import com.github.seratch.jslack.api.methods.response.oauth.OAuthAccessResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.vdda.jpa.Oauth;
import com.vdda.repository.OauthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OauthController {

    private final Slack slack = Slack.getInstance();
    private final String clientId = System.getenv("SLACK_CLIENT_ID");
    private final String clientSecret = System.getenv("SLACK_CLIENT_SECRET");

    private OauthRepository oauthRepository;

    @Autowired
    public OauthController(OauthRepository oauthRepository) {
        this.oauthRepository = oauthRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String processOauth(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error) {

        log.debug("code = [" + code + "], error = [" + error + "]");

        if (error != null) {
            throw new IllegalArgumentException("Oauth process cancelled - error");
        }

        if (code == null) {
            throw new IllegalArgumentException("Oauth process cancelled - no code");
        }

        OAuthAccessResponse oAuthAccessResponse = getOauthAccessResponse(code);
        if (!oAuthAccessResponse.isOk()) {
            log.warn("Failed to complete oauth process: {}", oAuthAccessResponse.toString());
            throw new IllegalArgumentException("Failed to complete oauth process");
        }

        UsersIdentityResponse usersIdentityResponse = getUsersIdentityResponse(oAuthAccessResponse.getAccessToken());
        if (!usersIdentityResponse.isOk()) {
            log.warn("Failed to complete oauth process: {}", usersIdentityResponse.toString());
            throw new IllegalArgumentException("Failed to complete oauth process");
        }

        oauthRepository.save(new Oauth(usersIdentityResponse.getTeam().getId(), oAuthAccessResponse.getAccessToken()));

        return "Yay!";
    }

    private OAuthAccessResponse getOauthAccessResponse(String code) {
        OAuthAccessResponse oAuthAccessResponse = new OAuthAccessResponse();

        try {
            oAuthAccessResponse = slack.methods().oauthAccess(
                    OAuthAccessRequest.builder()
                            .code(code)
                            .clientId(clientId)
                            .clientSecret(clientSecret)
                            .build());
        } catch (IOException | SlackApiException e) {
            log.debug("OAuthAccessRequest", e);
        }

        log.debug(oAuthAccessResponse.toString());
        return oAuthAccessResponse;
    }

    private UsersIdentityResponse getUsersIdentityResponse(String accessToken) {
        UsersIdentityResponse usersIdentityResponse = new UsersIdentityResponse();

        try {
            usersIdentityResponse = slack.methods().usersIdentity(
                    UsersIdentityRequest.builder()
                            .token(accessToken)
                            .build());
        } catch (IOException | SlackApiException e) {
            log.debug("UsersIdentityRequest", e);
        }

        log.debug(usersIdentityResponse.toString());

        return usersIdentityResponse;
    }
}
