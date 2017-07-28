package com.implemica.CalculatorProject.calculation;


/**
 * The enum contains an operations with memorized value such as add or subtract to memorized
 * value, store value, recall or clean it.
 *
 * @author V, Kozina-Kravchenko
 */
public enum MemoryOperation {

    MEMORY_CLEAN("MC"), MEMORY_RECALL("MR"), MEMORY_ADD("M+"), MEMORY_SUBTRACT("M-"), MEMORY_STORE("MS"),
    MEMORY_SHOW("M\uD83E\uDC93");

    /**
     * A string representation of the operation.
     */
    private String code;

    /**
     * Constructs a new {@code MemoryOperation} with the given string representation.
     *
     * @param code a string representation of the operation
     */
    MemoryOperation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns the {@code MemoryOperation} instance with the specified operation code. If not found returns null.
     *
     * @param operationCode a code to search an operation
     * @return the {@code MemoryOperation} instance with the specified operation code. If not found returns null
     */
    public static MemoryOperation getOperation(String operationCode) {
        for (MemoryOperation operation : values()) {
            if (operation.code.equals(operationCode)) {
                return operation;
            }
        }
        return null;
    }
}
