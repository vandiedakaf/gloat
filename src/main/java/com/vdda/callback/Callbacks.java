package com.vdda.callback;

/**
 * Created by francois
 * on 2017-01-01
 * for vandiedakaf solutions
 */
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
