package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.calculation.StandardCalculatorTest;
import com.implemica.CalculatorProject.calculation.OperationTest;
import com.implemica.CalculatorProject.controller.CalculatorControllerTest;
import com.implemica.CalculatorProject.processing.InputValueProcessorTest;
import com.implemica.CalculatorProject.util.OutputFormatterTest;
import com.implemica.CalculatorProject.util.ValueTransformerUtilTest;
import com.implemica.CalculatorProject.validation.DataValidatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
        OperationTest.class,
        StandardCalculatorTest.class,
        ValueTransformerUtilTest.class,
        DataValidatorTest.class,
        OutputFormatterTest.class,
        InputValueProcessorTest.class,
        CalculatorControllerTest.class
})
@RunWith(Suite.class)
public class AllTests {
}
