package com.vdda.callback;

public enum Callbacks {
    CONFIRM_DRAW("confirm_draw"),
    CONFIRM_LOSS("confirm_loss"),
    CONFIRM_VICTORY("confirm_victory");

    private String name;

    Callbacks(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
