package com.implemica.CalculatorProject.model.calculation;

import java.math.BigDecimal;

/**
 * The enum contains operations for editing {@link BigDecimal} numbers: reset current number to {@link BigDecimal#ZERO},
 * reset all numbers to {@link BigDecimal#ZERO} or delete last digit in number.
 *
 * @author V. Kozina-Kravchenko
 */
public enum EditOperation {

    /**
     * Constant represents an edit operation for resetting current number to zero value.
     */
    CLEAN_CURRENT("CE"),

    /**
     * Constant represents an edit operation for resetting all numbers to zero values.
     */
    CLEAN("C"),

    /**
     * Constant represents an edit operation for deleting last digit in current number.
     */
    LEFT_ERASE("⌫");

    /**
     * A string representation of an {@link EditOperation}.
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
