package com.implemica.CalculatorProject.model.calculation;

import org.junit.Assert;
import org.junit.Test;

public class OperationTest {

    @Test
    public void testGetOperation() {

        // Mathematical operations
        for (MathOperation operation : MathOperation.values()) {
            testGetMathOperation(operation, operation.name());
        }
        testGetMathOperation(null, null);
        testGetMathOperation(null, "");
        testGetMathOperation(null, "some string");

        // Memory operations
        for (MemoryOperation operation : MemoryOperation.values()) {
            testGetMemoryOperation(operation, operation.name());
        }
        testGetMemoryOperation(null, null);
        testGetMemoryOperation(null, "");
        testGetMemoryOperation(null, "some string");
    }

    private void testGetMathOperation(MathOperation expected, String code) {
        Assert.assertEquals(expected, MathOperation.getOperation(code));
    }

    private void testGetMemoryOperation(MemoryOperation expected, String code) {
        Assert.assertEquals(expected, MemoryOperation.getOperation(code));
    }
}