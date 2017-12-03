package com.vdda.slack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Attachment {
    private List<Action> actions;
    private String callback_id;
    private String color;
    private String fallback;
    private List<String> mrkdwn_in;
    private String text;
    private String thumb_url;
    private String title;
    private List<Field> fields;
}
