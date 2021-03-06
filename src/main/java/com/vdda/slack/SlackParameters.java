package com.vdda.slack;

public enum SlackParameters {
    CHANNEL_ID("channel_id"),
    USER_ID("user_id"),
    RESPONSE_URL("response_url"),
    TEAM_ID("team_id"),
    TEXT("text"),
    TOKEN("token"),;

    String name;
    SlackParameters(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
