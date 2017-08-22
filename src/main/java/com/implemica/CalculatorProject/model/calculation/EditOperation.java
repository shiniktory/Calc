package com.implemica.CalculatorProject.model.calculation;

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
    private final String symbol;

    /**
     * Constructs a new {@code EditOperation} with the given symbol.
     *
     * @param symbol a string representation of an operation
     */
    EditOperation(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
