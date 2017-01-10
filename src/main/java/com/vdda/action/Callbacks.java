package com.vdda.action;

/**
 * Created by francois
 * on 2017-01-01
 * for vandiedakaf solutions
 */
public enum Callbacks {
    VICTORY_CONFIRM("victory_confirm");

    private String name;

    Callbacks(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
