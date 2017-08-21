package com.implemica.CalculatorProject.model.calculation;

import com.implemica.CalculatorProject.model.validation.DataValidator;

import static com.implemica.CalculatorProject.model.validation.DataValidator.isEmptyString;

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
    private final String symbol;

    /**
     * The flag variable shows is operation binary (true) or unary (false).
     */
    private final boolean isBinary;

    /**
     * Constructs a new {@code MathOperation} with the given description.
     *
     * @param symbol a string representation of the operation
     * @param isBinary the flag shows is operation binary or unary
     */
    MathOperation(String symbol, boolean isBinary) {
        this.symbol = symbol;
        this.isBinary = isBinary;
    }

    /**
     * Returns the {@code MathOperation} instance with the specified operation symbol. If not found returns null.
     *
     * @param operationId a symbol to search an operation
     * @return the {@code MathOperation} instance with the specified operation symbol. If not found returns null
     */
    public static MathOperation getOperation(String operationId) {
        MathOperation operation = null;
        try {
            operation = MathOperation.valueOf(operationId.toUpperCase().trim());
        } catch (Exception e) {
            // if no operation found return null
        }
        return operation;
    }

    public String symbol() {
        return symbol;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public String id() {
        return name().toLowerCase();
    }
}
