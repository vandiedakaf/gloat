package com.vdda.slack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-27
 * for vandiedakaf solutions
 */
@Getter
@Setter
public class Attachment {
    public String text;
    public String color;
    public List<String> mrkdwn_in;
}
