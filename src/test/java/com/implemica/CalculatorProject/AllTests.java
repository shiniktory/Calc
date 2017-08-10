package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.calculation.StandardCalculatorTest;
import com.implemica.CalculatorProject.calculation.OperationTest;
import com.implemica.CalculatorProject.controller.TestView;
import com.implemica.CalculatorProject.util.OutputFormatterTest;
import com.implemica.CalculatorProject.util.ValueTransformerUtilTest;
import com.implemica.CalculatorProject.validation.DataValidatorTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.testfx.framework.junit.ApplicationTest.launch;

@Suite.SuiteClasses({
        OperationTest.class,
        StandardCalculatorTest.class,
        ValueTransformerUtilTest.class,
        DataValidatorTest.class,
        OutputFormatterTest.class,
        TestView.class
})
@RunWith(Suite.class)
public class AllTests {

    @BeforeClass
    public static void setUpInit() throws Exception {
        launch(CalcApplication.class);
    }
}
