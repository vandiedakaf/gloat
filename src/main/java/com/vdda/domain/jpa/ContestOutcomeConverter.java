package com.vdda.domain.jpa;

import javax.persistence.AttributeConverter;

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
