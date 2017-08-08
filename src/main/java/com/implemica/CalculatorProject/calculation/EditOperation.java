package com.implemica.CalculatorProject.calculation;

/**
 * The enum contains operations for editing fields with numbers such as reset current number,
 * reset all numbers or delete last digit.
 *
 * @author V. Kozina-Kravchenko
 */
public enum EditOperation {

    CLEAN_CURRENT("CE"), CLEAN("C"), LEFT_ERASE("⌫");

    /**
     * A string representation of an operation.
     */
    private final String code;

    /**
     * Constructs a new {@code EditOperation} with the given code.
     *
     * @param code a string representation of an operation
     */
    EditOperation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
