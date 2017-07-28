package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.exception.CalculationException;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.util.OutputFormatter.removeGroupDelimiters;
import static com.implemica.CalculatorProject.validation.DataValidator.isEmptyString;
import static com.implemica.CalculatorProject.validation.DataValidator.isNumber;

public class ValueTransformerUtil {

    public static BigDecimal[] getBigDecimalValues(String... strValues) throws CalculationException {
        if (strValues == null) {
            return null;
        }

        BigDecimal[] numbers = new BigDecimal[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            if (!isNumber(strValues[i])) {
                throw new CalculationException("Some of arguments is not valid. Argument index = " + i +
                ", value = " + strValues[i]);
            }
            numbers[i] = new BigDecimal(removeGroupDelimiters(strValues[i]));
        }
        return numbers;
    }
}
