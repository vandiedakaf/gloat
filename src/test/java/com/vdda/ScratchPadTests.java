package com.vdda;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by francois on 2016-12-10 for
 * vandiedakaf solutions
 */
public class ScratchPadTests {

    @Test
    public void getUsers() throws Exception {
        String token = System.getenv("SLACK_TOKEN");
        Slack slack = Slack.getInstance();

        UsersListResponse usersListResponse = slack.methods().usersList(
                UsersListRequest.builder()
                        .token(token)
                        .presence(1)
                        .build());

        assertTrue(usersListResponse.isOk());
    }
}
