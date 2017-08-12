package com.vdda.slack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Attachment {
    public List<Action> actions;
    public String callback_id;
    public String color;
    public String fallback;
    public List<String> mrkdwn_in;
    public String text;
    public String thumb_url;
    public String title;
}
