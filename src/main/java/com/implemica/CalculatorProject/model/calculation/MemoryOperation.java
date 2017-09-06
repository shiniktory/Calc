package com.implemica.CalculatorProject.model.calculation;


/**
 * The enum contains an operations with memorized values: add to or subtract from a memorized
 * value, store value, recall from memory or clean it.
 *
 * @author V. Kozina-Kravchenko
 */
public enum MemoryOperation {
    // todo javadocs to enum elements
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
}
