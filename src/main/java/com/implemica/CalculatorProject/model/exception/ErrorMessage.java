package com.implemica.CalculatorProject.model.exception;

import com.implemica.CalculatorProject.model.calculation.MathOperation;

import java.math.BigDecimal;

/**
 * The enum contains constants of error messages for exceptions that may occur in application.
 *
 * @author V. Kozina-Kravchenko
 */
public enum ErrorMessage {

    /**
     * The error message about division by zero occurs.
     */
    DIVISION_BY_ZERO,

    /**
     * An error message about situation when result is undefined.
     * For example, division zero by zero.
     */
    RESULT_IS_UNDEFINED,

    /**
     * The error message about invalid input that means an input {@link BigDecimal} number is not allowed for the current
     * {@link MathOperation}. For example, negative number for {@link MathOperation#SQUARE_ROOT} operation.
     */
    INVALID_INPUT,

    /**
     * An error message about {@link BigDecimal} number's value is too large or too small.
     */
    OVERFLOW
}
