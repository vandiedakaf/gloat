package com.vdda;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by francois
 * on 2017-02-21
 * for vandiedakaf solutions
 */
@ConfigurationProperties(value = "env")
@Getter
@Setter
@Component
public class EnvProperties {

    private int eloInit;
}
