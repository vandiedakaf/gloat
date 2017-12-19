package com.vdda.domain.jpa;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ContestOutcomeConverterTest {

    private ContestOutcomeConverter contestOutcomeConverter;

    @Before
    public void setUp() throws Exception {
        contestOutcomeConverter = new ContestOutcomeConverter();
    }

    @Test
    public void convertToDatabaseColumn() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(contestOutcomeConverter.convertToDatabaseColumn(c), is((c.getKey()))));
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(contestOutcomeConverter.convertToEntityAttribute(c.getKey()), is((c))));

    }
}
