package com.vdda;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value = "env")
@Getter
//@Setter
@Component
public class EnvProperties {

    // TODO replace with config server
    private int eloInit;
}
