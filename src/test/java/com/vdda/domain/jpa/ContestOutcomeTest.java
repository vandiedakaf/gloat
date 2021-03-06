package com.vdda.domain.jpa;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContestOutcomeTest {

    @Test
    public void getByKey() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(c, is((ContestOutcome.getEnumByKey(c.getKey())))));
    }
}
