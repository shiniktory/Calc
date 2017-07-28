package com.implemica.CalculatorProject.calculation;

import org.junit.Assert;
import org.junit.Test;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;

public class OperationTest {

    @Test
    public void testGetOperation() {

        // Mathematical operations
        testGetMathOperation(ADD, ADD.getCode());
        testGetMathOperation(SUBTRACT, SUBTRACT.getCode());
        testGetMathOperation(MULTIPLY, MULTIPLY.getCode());
        testGetMathOperation(DIVIDE, DIVIDE.getCode());
        testGetMathOperation(NEGATE, NEGATE.getCode());
        testGetMathOperation(PERCENT, PERCENT.getCode());
        testGetMathOperation(SQUARE, SQUARE.getCode());
        testGetMathOperation(SQUARE_ROOT, SQUARE_ROOT.getCode());
        testGetMathOperation(REVERSE, REVERSE.getCode());
        testGetMathOperation(null, "some string");
        testGetMathOperation(null, null);

        // Memory operations
        testGetMemoryOperation(MEMORY_CLEAN, MEMORY_CLEAN.getCode());
        testGetMemoryOperation(MEMORY_RECALL, MEMORY_RECALL.getCode());
        testGetMemoryOperation(MEMORY_ADD, MEMORY_ADD.getCode());
        testGetMemoryOperation(MEMORY_SUBTRACT, MEMORY_SUBTRACT.getCode());
        testGetMemoryOperation(MEMORY_STORE, MEMORY_STORE.getCode());
        testGetMemoryOperation(null, "some string");
        testGetMemoryOperation(null, null);

    }

    private void testGetMathOperation(MathOperation expected, String code) {
        Assert.assertEquals(expected, MathOperation.getOperation(code));
    }

    private void testGetMemoryOperation(MemoryOperation expected, String code) {
        Assert.assertEquals(expected, MemoryOperation.getOperation(code));
    }
}
