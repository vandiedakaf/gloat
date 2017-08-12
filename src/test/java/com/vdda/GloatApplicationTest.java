package com.vdda;

import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

public class GloatApplicationTest {

    @Mocked
    SpringApplication springApplication;

    @Test
    public void runGolden() throws Exception {
        String[] args = {"some", "arguments"};
        GloatApplication.main(args);

        new Verifications() {{
           springApplication.run(GloatApplication.class, args);
        }};
    }
}
