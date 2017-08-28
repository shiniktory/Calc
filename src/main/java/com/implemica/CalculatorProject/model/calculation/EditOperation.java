package com.implemica.CalculatorProject.model.calculation;

import java.math.BigDecimal;

/**
 * The enum contains operations for editing {@link BigDecimal} numbers: reset current number to {@link BigDecimal#ZERO},
 * reset all numbers to {@link BigDecimal#ZERO} or delete last digit in number.
 *
 * @author V. Kozina-Kravchenko
 */
public enum EditOperation {

    CLEAN_CURRENT("CE"), CLEAN("C"), LEFT_ERASE("⌫");

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
