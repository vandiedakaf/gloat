package com.vdda.jpa;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Created by francois
 * on 2017-02-19
 * for vandiedakaf solutions
 */
public class ContestOutcomeTest {

    @Test
    public void getByKey() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(c, is(equalTo(ContestOutcome.getEnumByKey(c.getKey())))));
    }
}
