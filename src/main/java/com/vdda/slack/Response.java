package com.vdda.slack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-27
 * for vandiedakaf solutions
 */
@Setter
@Getter
public class Response {
    public String text;
    public List<Attachment> attachments;
}
