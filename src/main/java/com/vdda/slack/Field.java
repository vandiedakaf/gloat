package com.vdda.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {

    private String title;
    private String value;
	@JsonProperty("short")
    private boolean shortMessage;
}
