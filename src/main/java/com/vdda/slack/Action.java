package com.vdda.slack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Action {
    private static final String BUTTON = "button";

    private String name;
    private String text;
    @Setter(AccessLevel.NONE)
    private String type = BUTTON;
    private String value;
    private String style;
}
