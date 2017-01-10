package com.vdda.slack;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    public String text;
    public List<Attachment> attachments;
}
