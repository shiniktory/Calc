package com.implemica.CalculatorProject.calculation;

/**
 * The enum contains a Mathematical operation types with its string representations.
 *
 * @author V. Kozina-Kravchenko
 */
public enum MathOperation {

    ADD("+", true), SUBTRACT("−", true), MULTIPLY("×", true), DIVIDE("÷", true),
    NEGATE("±", false), PERCENT("%", true), SQUARE_ROOT("√", false), SQUARE("\uD835\uDC65²", false),
    REVERSE("¹⁄\uD835\uDC65", false), RESULT("=", true);

    /**
     * A string representation of the operation.
     */
    private final String code;

    /**
     * The flag variable shows is operation binary (true) or unary (false).
     */
    private final boolean isBinary;

    /**
     * Constructs a new {@code MathOperation} with the given description.
     *
     * @param code a string representation of the operation
     * @param isBinary the flag shows is operation binary or unary
     */
    MathOperation(String code, boolean isBinary) {
        this.code = code;
        this.isBinary = isBinary;
    }

    /**
     * Returns the {@code MathOperation} instance with the specified operation code. If not found returns null.
     *
     * @param operationCode a code to search an operation
     * @return the {@code MathOperation} instance with the specified operation code. If not found returns null
     */
    public static MathOperation getOperation(String operationCode) {
        for (MathOperation operation : values()) {
            if (operation.code.equals(operationCode)) {
                return operation;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public boolean isBinary() {
        return isBinary;
    }
}
