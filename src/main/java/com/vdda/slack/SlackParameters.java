package com.vdda.slack;

/**
 * Created by francois
 * on 2016-12-28
 * for vandiedakaf solutions
 */
public enum SlackParameters {
    CHANNEL_ID("channel_id"),
    USER_ID("user_id"),
    TEAM_ID("team_id"),
    TEXT("text");

    String name;
    SlackParameters(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
