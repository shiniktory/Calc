package com.implemica.CalculatorProject;


import com.implemica.CalculatorProject.model.Calculator;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;

public class ModelDemo {

    public static void main(String[] args) throws CalculationException {
        Calculator valueProcessor = new Calculator();

        // 5 + 10.2 = 15.2
        valueProcessor.enterDigit(BigDecimal.valueOf(5));

        valueProcessor.executeMathOperation(MathOperation.ADD);

        valueProcessor.enterDigit(BigDecimal.ONE);
        valueProcessor.enterDigit(BigDecimal.ZERO);
        valueProcessor.addPoint();
        valueProcessor.enterDigit(BigDecimal.valueOf(2));

        BigDecimal result = valueProcessor.calculateResult();
        System.out.println("5 + 10.2 = " + result);

        valueProcessor.cleanAll();

        // 25 √ = 5
        valueProcessor.enterDigit(BigDecimal.valueOf(2));
        valueProcessor.enterDigit(BigDecimal.valueOf(5));

        valueProcessor.executeMathOperation(MathOperation.SQUARE_ROOT);

        result = valueProcessor.calculateResult();
        System.out.println("√25 = " + result);

        valueProcessor.cleanCurrent();

        // get last entered number
        valueProcessor.enterDigit(BigDecimal.valueOf(9));
        valueProcessor.enterDigit(BigDecimal.valueOf(5));
        BigDecimal lastEnteredNumber = valueProcessor.getLastNumber();
        System.out.println("Last entered number: " + lastEnteredNumber);

        // delete last digit
        valueProcessor.deleteLastDigit();
        lastEnteredNumber = valueProcessor.getLastNumber();
        System.out.println("Last entered number after deleting last digit: " + lastEnteredNumber);

        // memorize last entered number
        valueProcessor.executeMemoryOperation(MemoryOperation.MEMORY_STORE);
        valueProcessor.cleanAll();
        lastEnteredNumber = valueProcessor.getLastNumber();
        System.out.println("Last entered number after cleaning: " + lastEnteredNumber);

        // get memorized number
        valueProcessor.executeMemoryOperation(MemoryOperation.MEMORY_RECALL);
        BigDecimal recalledMemorizedNumber = valueProcessor.getLastNumber();
        System.out.println("Memorized number: " + recalledMemorizedNumber);

    }
}
