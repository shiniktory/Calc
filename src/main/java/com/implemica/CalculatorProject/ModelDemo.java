package com.implemica.CalculatorProject;


import com.implemica.CalculatorProject.model.Calculator;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.calculation.StandardCalculationExecutor;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;

public class ModelDemo {

    public static void main(String[] args) throws CalculationException {
        Calculator calculator = new Calculator();
        calculator.setCalculationExecutor(new StandardCalculationExecutor());

        // 5 + 10.2 = 15.2
        calculator.enterDigit(BigDecimal.valueOf(5));

        calculator.executeMathOperation(MathOperation.ADD);

        calculator.enterDigit(BigDecimal.ONE);
        calculator.enterDigit(BigDecimal.ZERO);
        calculator.addPoint();
        calculator.enterDigit(BigDecimal.valueOf(2));

        BigDecimal result = calculator.calculateResult();
        System.out.println("5 + 10.2 = " + result);

        calculator.cleanAll();

        // 25 √ = 5
        calculator.enterDigit(BigDecimal.valueOf(2));
        calculator.enterDigit(BigDecimal.valueOf(5));

        calculator.executeMathOperation(MathOperation.SQUARE_ROOT);

        result = calculator.calculateResult();
        System.out.println("√25 = " + result);

        calculator.cleanCurrent();

        // get last entered number
        calculator.enterDigit(BigDecimal.valueOf(9));
        calculator.enterDigit(BigDecimal.valueOf(5));
        BigDecimal lastEnteredNumber = calculator.getLastNumber();
        System.out.println("Last entered number: " + lastEnteredNumber);

        // delete last digit
        calculator.deleteLastDigit();
        lastEnteredNumber = calculator.getLastNumber();
        System.out.println("Last entered number after deleting last digit: " + lastEnteredNumber);

        // memorize last entered number
        calculator.executeMemoryOperation(MemoryOperation.MEMORY_STORE);
        calculator.cleanAll();
        lastEnteredNumber = calculator.getLastNumber();
        System.out.println("Last entered number after cleaning: " + lastEnteredNumber);

        // get memorized number
        calculator.executeMemoryOperation(MemoryOperation.MEMORY_RECALL);
        BigDecimal recalledMemorizedNumber = calculator.getLastNumber();
        System.out.println("Memorized number: " + recalledMemorizedNumber);

    }
}
