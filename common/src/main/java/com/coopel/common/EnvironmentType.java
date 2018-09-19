package com.coopel.common;

public enum EnvironmentType {

    Default(true),
    Production(false),
    Test(true),
    Development(true);

    private final boolean safe;

    EnvironmentType(boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }
}
