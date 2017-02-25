package com.vdda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
public class GloatApplication {

	public static void main(String[] args) {
		SpringApplication.run(GloatApplication.class, args);
	}
}
