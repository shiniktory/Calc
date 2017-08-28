package com.implemica.CalculatorProject.model.calculation;

import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;

/**
 * The CalculatorExecutor interface grants a functionality to perform calculations for some Mathematical operations.
 *
 * @author V. Kozina-Kravchenko
 */
public interface CalculationExecutor {

    /**
     * Returns the result of Mathematical calculations for the given {@link BigDecimal} numbers.
     *
     * @param operation a Mathematical operation to perform with the given numbers
     * @param numbers   a numbers to perform a Mathematical operation with
     * @return the result of Mathematical calculations for the given {@link BigDecimal} numbers
     * @throws CalculationException if some error occurs during the calculations
     */
    BigDecimal calculate(MathOperation operation, BigDecimal... numbers) throws CalculationException;
}
