package com.vdda.callback;

public enum Callbacks {
    CONFIRM_SERIES("confirm_series");

    private String name;

    Callbacks(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
