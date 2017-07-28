package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;

import java.math.BigDecimal;

/**
 * The {@code Calculator} interface grants a functionality to calculate some Mathematical operations.
 *
 * @author V. Kozina-Kravchenko
 */
public interface Calculator {

    /**
     * Returns the result of Mathematical calculations.
     *
     * @return the result of Mathematical calculations
     * @throws CalculationException if some error occurs during the calculations
     */
    BigDecimal calculate() throws CalculationException;
}
