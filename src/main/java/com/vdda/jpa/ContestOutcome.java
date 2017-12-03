package com.vdda.jpa;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ContestOutcome {
    WIN("w", "l", "wins"),
    LOSS("l", "w", "losses"),
    DRAW("d", "d", "draws");

    @Getter
    private final String key;
	@Getter
    private final String opposite;
	@Getter
	private final String plural;

    private static Map<String, ContestOutcome> contestOutcomes = new HashMap<>();

    static {
        for (ContestOutcome c : ContestOutcome.values())
            contestOutcomes.put(c.getKey(), c);
    }

	ContestOutcome(String key, String opposite, String plural) {
        this.key = key;
        this.opposite = opposite;
        this.plural = plural;
    }

    public static ContestOutcome getEnumByKey(String key) {
        return contestOutcomes.get(key);
    }

	public ContestOutcome getOppositeEnum() {
		return contestOutcomes.get(opposite);
	}
}
