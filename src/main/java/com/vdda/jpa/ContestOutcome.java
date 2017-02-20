package com.vdda.jpa;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by francois
 * on 2017-02-15
 * for vandiedakaf solutions
 */
public enum ContestOutcome {
    WIN("w"),
    LOSS("l"),
    DRAW("d");

    private final String key;
    private static Map<String, ContestOutcome> contestOutcomes = new HashMap<>();

    static {
        for (ContestOutcome c : ContestOutcome.values())
            contestOutcomes.put(c.getKey(), c);
    }

    ContestOutcome(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ContestOutcome getEnumByKey(String key) {
        return contestOutcomes.get(key);
    }
}
