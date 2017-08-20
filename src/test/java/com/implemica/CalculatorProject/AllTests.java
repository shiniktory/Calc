package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.model.calculation.StandardCalculatorTest;
import com.implemica.CalculatorProject.model.calculation.OperationTest;
import com.implemica.CalculatorProject.controller.TestView;
import com.implemica.CalculatorProject.model.formatting.OutputFormatterTest;
import com.implemica.CalculatorProject.model.validation.DataValidatorTest;
import com.implemica.CalculatorProject.view.CalculatorApplication;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.testfx.framework.junit.ApplicationTest.launch;

@Suite.SuiteClasses({
        OperationTest.class,
        StandardCalculatorTest.class,
        DataValidatorTest.class,
        OutputFormatterTest.class,
        TestView.class
})
@RunWith(Suite.class)
public class AllTests {

    @BeforeClass
    public static void setUpInit() throws Exception {
        launch(CalculatorApplication.class);
    }
}
