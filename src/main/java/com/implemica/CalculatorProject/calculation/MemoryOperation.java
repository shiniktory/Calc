package com.implemica.CalculatorProject.calculation;


/**
 * The enum contains an operations with memorized value such as add or subtract to memorized
 * value, store value, recall or clean it.
 *
 * @author V. Kozina-Kravchenko
 */
public enum MemoryOperation {

    MEMORY_CLEAN("MC"), MEMORY_RECALL("MR"), MEMORY_ADD("M+"), MEMORY_SUBTRACT("M-"), MEMORY_STORE("MS"),
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

    public String id() {
        return name().toLowerCase();
    }

    /**
     * Returns the {@code MemoryOperation} instance with the specified operation symbol. If not found returns null.
     *
     * @param operationId a symbol to search an operation
     * @return the {@code MemoryOperation} instance with the specified operation symbol. If not found returns null
     */
    public static MemoryOperation getOperation(String operationId) {
        for (MemoryOperation operation : values()) {
            if (operation.name().equalsIgnoreCase(operationId)) {
                return operation;
            }
        }
        return null;
    }
}
