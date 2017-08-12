package com.vdda.jpa;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ContestOutcomeConverterTest {

    private ContestOutcomeConverter contestOutcomeConverter;

    @Before
    public void setUp() throws Exception {
        contestOutcomeConverter = new ContestOutcomeConverter();
    }

    @Test
    public void convertToDatabaseColumn() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(contestOutcomeConverter.convertToDatabaseColumn(c), is(equalTo(c.getKey()))));
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        Arrays.stream(ContestOutcome.values()).forEach(c -> assertThat(contestOutcomeConverter.convertToEntityAttribute(c.getKey()), is(equalTo(c))));

    }
}
