package com.vdda.controller;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.oauth.OAuthAccessRequest;
import com.github.seratch.jslack.api.methods.request.team.TeamInfoRequest;
import com.github.seratch.jslack.api.methods.response.oauth.OAuthAccessResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.vdda.jpa.Oauth;
import com.vdda.repository.OauthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@Controller
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

        OAuthAccessResponse oAuthAccessResponse = getOauthAccess(code);
        if (!oAuthAccessResponse.isOk()) {
            log.warn("Failed to complete oauth process: {}", oAuthAccessResponse.toString());
            throw new IllegalArgumentException("Failed to complete oauth process");
        }


        TeamInfoResponse teamInfoResponse = getTeamInfo(oAuthAccessResponse.getAccessToken());
        if (!teamInfoResponse.isOk()) {
            log.warn("Failed to get team info: {}", teamInfoResponse.toString());
            throw new IllegalArgumentException("Failed to get team info");
        }

        oauthRepository.save(new Oauth(teamInfoResponse.getTeam().getId(), oAuthAccessResponse.getAccessToken()));

        return "oauth";
    }

    private OAuthAccessResponse getOauthAccess(String code) {
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

    private TeamInfoResponse getTeamInfo(String accessToken) {
        TeamInfoResponse teamInfoResponse = new TeamInfoResponse();

        try {
            teamInfoResponse = slack.methods().teamInfo(
                    TeamInfoRequest.builder()
                            .token(accessToken)
                            .build());
        } catch (IOException | SlackApiException e) {
            log.debug("TeamInfoRequest", e);
        }

        log.debug(teamInfoResponse.toString());

        return teamInfoResponse;
    }
}
