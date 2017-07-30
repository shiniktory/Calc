package com.implemica.CalculatorProject.calculation;

public enum EditOperation {

    CLEAN_CURRENT("CE"), CLEAN("C"), LEFT_ERASE("⌫");

    private String code;

    EditOperation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
