package com.implemica.CalculatorProject;


import com.implemica.CalculatorProject.model.InputValueProcessor;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;

public class ModelDemo {

    public static void main(String[] args) throws CalculationException {
        InputValueProcessor valueProcessor = new InputValueProcessor();

        // 5 + 10.2 = 15.2
        valueProcessor.enterDigit(5);

        valueProcessor.executeMathOperation(MathOperation.ADD);

        valueProcessor.enterDigit(1);
        valueProcessor.enterDigit(0);
        valueProcessor.addPoint();
        valueProcessor.enterDigit(2);

        BigDecimal result = valueProcessor.calculateResult();
        System.out.println("5 + 10.2 = " + result);

        valueProcessor.cleanAll();

        // 25 √ = 5
        valueProcessor.enterDigit(2);
        valueProcessor.enterDigit(5);

        valueProcessor.executeMathOperation(MathOperation.SQUARE_ROOT);

        result = valueProcessor.calculateResult();
        System.out.println("√25 = " + result);

        valueProcessor.cleanCurrent();

        // get last entered number
        valueProcessor.enterDigit(9);
        valueProcessor.enterDigit(5);
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
