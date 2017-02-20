package com.vdda.jpa;

import javax.persistence.AttributeConverter;

/**
 * Created by francois
 * on 2017-02-15
 * for vandiedakaf solutions
 */
public class ContestOutcomeConverter implements AttributeConverter<ContestOutcome, String> {

    @Override
    public String convertToDatabaseColumn(ContestOutcome contestOutcome) {
        return contestOutcome.getKey();
    }

    @Override
    public ContestOutcome convertToEntityAttribute(String value) {
        return ContestOutcome.getEnumByKey(value);
    }

}
