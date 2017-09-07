package com.implemica.CalculatorProject.model.calculation;


/**
 * The enum contains an operations with memorized values: add to or subtract from a memorized
 * value, store value, recall from memory or clean it.
 *
 * @author V. Kozina-Kravchenko
 */
public enum MemoryOperation {

    /**
     * Constant represents an operation for resetting a memorized number to zero value.
     */
    MEMORY_CLEAN("MC"),

    /**
     * Constant represents an operation for recalling a memorized value to the screen.
     */
    MEMORY_RECALL("MR"),

    /**
     * Constant represents an operation for adding to the memorized number another number value.
     */
    MEMORY_ADD("M+"),

    /**
     * Constant represents an operation for subtracting from the memorized number another number value.
     */
    MEMORY_SUBTRACT("M-"),

    /**
     * Constant represents an operation for storing new number as memorized.
     */
    MEMORY_STORE("MS"),

    /**
     * Constant represents an operation for showing panel with stored values.
     */
    MEMORY_SHOW("M\uD83E\uDC93");

    /**
     * A string representation of the operation.
     */
    private final String symbol;

    /**
     * Constructs a new {@code MemoryOperation} with the given string representation.
     *
     * @param symbol a string representation of the operation
     */
    MemoryOperation(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
