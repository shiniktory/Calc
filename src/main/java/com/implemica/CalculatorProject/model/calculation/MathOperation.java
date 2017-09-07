package com.implemica.CalculatorProject.model.calculation;

/**
 * The enum contains a mathematical operation types with its string representations.
 *
 * @author V. Kozina-Kravchenko
 */
public enum MathOperation {

    /**
     * Constant represents a binary mathematical operation add.
     */
    ADD("+", true),

    /**
     * Constant represents a binary mathematical operation subtract.
     */
    SUBTRACT("−", true),

    /**
     * Constant represents a binary mathematical operation multiply.
     */
    MULTIPLY("×", true),

    /**
     * Constant represents a binary mathematical operation divide.
     */
    DIVIDE("÷", true),

    /**
     * Constant represents an unary mathematical operation negate.
     */
    NEGATE("±", false),

    /**
     * Constant represents a binary mathematical operation percent.
     */
    PERCENT("%", true),

    /**
     * Constant represents an unary mathematical operation square root.
     */
    SQUARE_ROOT("√", false),

    /**
     * Constant represents an unary mathematical operation square.
     */
    SQUARE("\uD835\uDC65²", false),

    /**
     * Constant represents an unary mathematical operation reverse.
     */
    REVERSE("¹⁄\uD835\uDC65", false);

    /**
     * A string representation of the operation.
     */
    private final String symbol;

    /**
     * The flag variable shows is {@link MathOperation} binary (true) or unary (false).
     */
    private final boolean isBinary;

    /**
     * Constructs a new {@code MathOperation} with the given description.
     *
     * @param symbol   a string representation of the operation
     * @param isBinary the flag shows is operation binary or unary
     */
    MathOperation(String symbol, boolean isBinary) {
        this.symbol = symbol;
        this.isBinary = isBinary;
    }

    public String symbol() {
        return symbol;
    }

    public boolean isBinary() {
        return isBinary;
    }
}
