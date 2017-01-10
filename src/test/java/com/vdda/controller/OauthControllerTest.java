package com.vdda.controller;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.response.oauth.OAuthAccessResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
public class OauthControllerTest {

    private static final String TEAM_ID = "teamId";

    @Autowired
    private MockMvc mvc;

    @Mocked
    Slack slack;

    @Test
    public void emptyCommand() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("nothing", "nothing")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void error() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("error", "Something went wrong")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void oauthGolden() throws Exception {
        new Expectations() {{
            slack.methods().oauthAccess(withNotNull());
            result = mockOauthResponse();

            slack.methods().usersIdentity(withNotNull());
            result = mockUsersIdentityResponse();
        }};

        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("code", "code")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void oauthResponseFail() throws Exception {
        new Expectations() {{
            slack.methods().oauthAccess(withNotNull());
            result = mockOauthResponseFail();
        }};

        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("code", "code")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void oauthResponseException() throws Exception {
        new Expectations() {{
            slack.methods().oauthAccess(withNotNull());
            result = new IOException();
        }};

        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("code", "code")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void usersIdentityFail() throws Exception {
        new Expectations() {{
            slack.methods().oauthAccess(withNotNull());
            result = mockOauthResponse();

            slack.methods().usersIdentity(withNotNull());
            result = mockUsersIdentityResponseFail();
        }};

        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("code", "code")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void usersIdentityException() throws Exception {
        new Expectations() {{
            slack.methods().oauthAccess(withNotNull());
            result = mockOauthResponse();

            slack.methods().usersIdentity(withNotNull());
            result = new IOException();
        }};

        mvc.perform(MockMvcRequestBuilders
                .get("/oauth")
                .param("code", "code")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private OAuthAccessResponse mockOauthResponse() {
        OAuthAccessResponse oAuthAccessResponse = new OAuthAccessResponse();
        oAuthAccessResponse.setOk(true);
        oAuthAccessResponse.setAccessToken("accessToken");
        return oAuthAccessResponse;
    }

    private OAuthAccessResponse mockOauthResponseFail() {
        OAuthAccessResponse oAuthAccessResponse = new OAuthAccessResponse();
        oAuthAccessResponse.setOk(false);
        return oAuthAccessResponse;
    }

    private UsersIdentityResponse mockUsersIdentityResponse() {
        UsersIdentityResponse usersIdentityResponse = new UsersIdentityResponse();
        usersIdentityResponse.setOk(true);
        UsersIdentityResponse.Team team = new UsersIdentityResponse.Team();
        team.setId(TEAM_ID);
        usersIdentityResponse.setTeam(team);
        return usersIdentityResponse;
    }

    private UsersIdentityResponse mockUsersIdentityResponseFail() {
        UsersIdentityResponse usersIdentityResponse = new UsersIdentityResponse();
        usersIdentityResponse.setOk(false);
        return usersIdentityResponse;
    }

}
