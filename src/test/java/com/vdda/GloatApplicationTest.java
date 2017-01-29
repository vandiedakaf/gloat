package com.vdda;

import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

/**
 * Created by francois
 * on 2017-01-19
 * for vandiedakaf solutions
 */
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
