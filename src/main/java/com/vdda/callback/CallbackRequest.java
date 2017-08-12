package com.vdda.callback;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CallbackRequest {
    private List<Action> actions;
    private String callbackId;
    private Channel channel;
    private String responseUrl;
    private Team team;
    private String token;
    private User user;

}
