package com.vdda.slack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Action {
    private static final String BUTTON = "button";

    public String name;
    public String text;
    @Setter(AccessLevel.NONE)
    public String type = BUTTON;
    public String value;
    public String style;
}
