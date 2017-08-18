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
    private final String symbol;

    /**
     * Constructs a new {@code EditOperation} with the given symbol.
     *
     * @param symbol a string representation of an operation
     */
    EditOperation(String symbol) {
        this.symbol = symbol;
    }

    public static EditOperation getOperation(String operationId) {
        for (EditOperation operation : values()) {
            if (operation.id().equalsIgnoreCase(operationId)) {
                return operation;
            }
        }
        return null;
    }

    public String symbol() {
        return symbol;
    }

    public String id() {
        return name().toLowerCase();
    }
}
