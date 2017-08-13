package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.MINUS;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.util.OutputFormatter.removeGroupDelimiters;
import static com.implemica.CalculatorProject.validation.DataValidator.isNumber;
import static java.lang.String.format;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.PERIOD;
import static javafx.scene.input.KeyCode.getKeyCode;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class StandardCalculatorTest {

    private static final FxRobot robot = new FxRobot();

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";
    private static final String DIVISION_BY_ZERO_MESSAGE = "Cannot divide by zero";
    private static final String INVALID_INPUT_MESSAGE = "Invalid input";
    private static final String OVERFLOW_MESSAGE = "Overflow";

    private static final String ARGUMENT_DELIMITERS = "\\s+[=\\s]*";

    private static TextField currentNumberText, prevOperationsText;

    private static Map<String, Button> buttons = new LinkedHashMap<>();

    @BeforeClass
    public static void setUpInit() {
        WaitForAsyncUtils.waitForFxEvents();
        // init numbers
        for (int i = 0; i <= 9; i++) {
            String number = String.valueOf(i);
            addButton(number, number);
        }
        addButton(".", "point");
        addButton("history", "history");

        // init math operations
        for (MathOperation operation : MathOperation.values()) {
            String operationId = operation.id();
            addButton(operationId, operationId);
        }

        // init edit number operations
        for (EditOperation operation : EditOperation.values()) {
            String operationId = operation.id();
            addButton(operationId, operationId);
        }

        // init memory operations
        for (MemoryOperation operation : MemoryOperation.values()) {
            addButton(operation.symbol(), operation.id());
        }

        // text fields with current number and history
        currentNumberText = find("#currentNumberText");
        prevOperationsText = find("#prevOperationsText");
    }

    private static void addButton(String buttonId, String query) {
        buttons.put(buttonId, find("#" + query));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private static <T extends Node> T find(final String query) {
        T node = robot.lookup(query).query();
        assertNotNull(node);
        return node;
    }

    @Before
    public void setUp() {
        // reset everything before each test
        pushKey(KeyCode.ESCAPE);
    }

    @Test
    public void testEnterTooLongNumbers() {
        // enter long number with max length and try to add more digits
        // positive numbers
        testTooLongNumber("0.0000000000000001", "-0.0000000000000001",
                "-0.0000000000000001");
        testTooLongNumber("0.0000005555555555", "-0.0000005555555555",
                "-0.0000005555555555");
        testTooLongNumber("0.8888888888888888", "-0.8888888888888888",
                "-0.8888888888888888");
        testTooLongNumber("1.888888888888888", "-1.888888888888888",
                "-1.888888888888888");
        testTooLongNumber("1.999999999999999", "-1.999999999999999",
                "-1.999999999999999");
        testTooLongNumber("132,132.1321321321", "-132,132.1321321321",
                "-132,132.1321321321");
        testTooLongNumber("11,111,111,111,111.11", "-11,111,111,111,111.11",
                "-11,111,111,111,111.11");
        testTooLongNumber("9,999,999,999,999,999", "-9,999,999,999,999,999",
                "-9,999,999,999,999,999.");

        // negative numbers
        testTooLongNumber("-0.0000000000000001", "0.0000000000000001",
                "0.0000000000000001");
        testTooLongNumber("-0.0000005555555555", "0.0000005555555555",
                "0.0000005555555555");
        testTooLongNumber("-0.8888888888888888", "0.8888888888888888",
                "0.8888888888888888");
        testTooLongNumber("-1.888888888888888", "1.888888888888888",
                "1.888888888888888");
        testTooLongNumber("-1.999999999999999", "1.999999999999999",
                "1.999999999999999");
        testTooLongNumber("-132,132.1321321321", "132,132.1321321321",
                "132,132.1321321321");
        testTooLongNumber("-11,111,111,111,111.11", "11,111,111,111,111.11",
                "11,111,111,111,111.11");
        testTooLongNumber("-9,999,999,999,999,999", "9,999,999,999,999,999",
                "9,999,999,999,999,999.");
    }

    private void testTooLongNumber(String number, String negatePressed, String pointPressed) {
        pushKey(ESCAPE);
        enterNumber(removeGroupDelimiters(number));
        testAddMoreDigits(number);

        // negate number and try to add more digits
        fireButton(NEGATE.id());
        testAddMoreDigits(negatePressed);

        // add point and try to add more digits
        pushKey(PERIOD);
        testAddMoreDigits(pointPressed);
    }

    private void testAddMoreDigits(String expectedNumber) {
        for (int i = 0; i < 9; i++) {
            KeyCode digitKey = getKeyCode(String.valueOf(i));
            pushKey(digitKey);
            testCurrentText(expectedNumber);
        }
    }

    @Test
    public void testAddOperation() {
        // with zero arguments
        testCalculations("0 + 555000000 = 555,000,000", "0 + ");
        testCalculations("0 + 1000 = 1,000", "0 + ");
        testCalculations("0 + 100 = 100", "0 + ");
        testCalculations("0 + 0.6666666666666667 = 0.6666666666666667", "0 + ");
        testCalculations("0 + 0 = 0", "0 + ");
        testCalculations("0 + -0.6666666666666667 = -0.6666666666666667", "0 + ");
        testCalculations("0 + -100 = -100", "0 + ");
        testCalculations("0 + -1000 = -1,000", "0 + ");
        testCalculations("0 + -555000000 = -555,000,000", "0 + ");

        // with positive arguments
        testCalculations("9999999999999999 + 9999999999999999 = 2.e+16", "9999999999999999 + ");
        testCalculations("5 + 55550 = 55,555", "5 + ");
        testCalculations("0.6666666666666667 + 0.0000000000000003 = 0.666666666666667", "0.6666666666666667 + ");
        testCalculations("0.000005 + 0.0000000000005 = 0.0000050000005", "0.000005 + ");

        // with positive and negative arguments
        testCalculations("9999999999999999 + -9999999999999999 = 0", "9999999999999999 + ");
        testCalculations("1000000.5 + -50000000 = -48,999,999.5", "1000000.5 + ");
        testCalculations("1000000 + -50000000 = -49,000,000", "1000000 + ");
        testCalculations("100 + -50 = 50", "100 + ");
        testCalculations("5 + -5 = 0", "5 + ");
        testCalculations("0.05 + -0.05 = 0", "0.05 + ");
        testCalculations("0.000005 + -0.0000000000005 = 0.0000049999995", "0.000005 + ");

        // with negative and positive arguments
        testCalculations("-5 + 55550 = 55,545", "-5 + ");
        testCalculations("-100 + 50 = -50", "-100 + ");
        testCalculations("-2147483648 + 2147483647 = -1", "-2147483648 + ");

        // with both negative arguments
        testCalculations("-0.6 + -0.6 = -1.2", "-0.6 + ");
        testCalculations("-0.000005 + -0.0000000000005 = -0.0000050000005", "-0.000005 + ");
        testCalculations("-0.1 + -0.1 = -0.2", "-0.1 + ");
        testCalculations("-1000000 + -50000000 = -51,000,000", "-1000000 + ");
        testCalculations("-1000000 + -50000000.5 = -51,000,000.5", "-1000000 + ");
        testCalculations("-9999999999999999 + -9999999999999999 = -2.e+16", "-9999999999999999 + ");

        // with more than one operation in sequence
        testCalculations("-2 + 3 + 5 = 6", "-2 + 3 + ");
        testCalculations("0 + 0 + 0 = 0", "0 + 0 + ");
        testCalculations("0 + 5 / 500 = 0.01", "0 + 5 ÷ ");
        testCalculations("2 + 3 + 5 = 10", "2 + 3 + ");
        testCalculations("5000 + -6 + 50 = 5,044", "5000 + -6 + ");
        testCalculations("999999999999999 + 1 / 2 = 500,000,000,000,000", "999999999999999 + 1 ÷ ");
        testCalculations("9999999999999999 + 1 / 2 = 5,000,000,000,000,000", "9999999999999999 + 1 ÷ ");
    }

    @Test
    public void testSubtractOperation() {
        // with zero arguments
        testCalculations("0 - 555000000 = -555,000,000", "0 − ");
        testCalculations("0 - 1000 = -1,000", "0 − ");
        testCalculations("0 - 100 = -100", "0 − ");
        testCalculations("0 - 0.6666666666666667 = -0.6666666666666667", "0 − ");
        testCalculations("0 - 0 = 0", "0 − ");
        testCalculations("0 - -0.6666666666666667 = 0.6666666666666667", "0 − ");
        testCalculations("0 - -100 = 100", "0 − ");
        testCalculations("0 - -1000 = 1,000", "0 − ");
        testCalculations("0 - -555000000 = 555,000,000", "0 − ");

        // with positive arguments
        testCalculations("9999999999999999 - 9999999999999999 = 0", "9999999999999999 − ");
        testCalculations("9999999999999999 - -9999999999999999 = 2.e+16", "9999999999999999 − ");
        testCalculations("2147483647 - 2147483647 = 0", "2147483647 − ");
        testCalculations("5 - 55550 = -55,545", "5 − ");
        testCalculations("0.6666666666666667 - 0.0000000000000003 = 0.6666666666666664", "0.6666666666666667 − ");
        testCalculations("0.000005 - 0.0000000000005 = 0.0000049999995", "0.000005 − ");

        //with positive and negative arguments
        testCalculations("1000000.5 - -50000000 = 51,000,000.5", "1000000.5 − ");
        testCalculations("1000000 - -50000000 = 51,000,000", "1000000 − ");
        testCalculations("100 - -50 = 150", "100 − ");
        testCalculations("5 - -5 = 10", "5 − ");
        testCalculations("0.05 - -0.05 = 0.1", "0.05 − ");
        testCalculations("0.000005 - -0.0000000000005 = 0.0000050000005", "0.000005 − ");

        // with negative and positive arguments
        testCalculations("-5 - 55550 = -55,555", "-5 − ");
        testCalculations("-1000000.5 - 50000000 = -51,000,000.5", "-1000000.5 − ");
        testCalculations("-1000000 - 50000000 = -51,000,000", "-1000000 − ");
        testCalculations("-100 - 50 = -150", "-100 − ");
        testCalculations("-5 - 5 = -10", "-5 − ");
        testCalculations("-0.05 - 0.05 = -0.1", "-0.05 − ");
        testCalculations("-0.000005 - 0.0000000000005 = -0.0000050000005", "-0.000005 − ");

        // with negative arguments
        testCalculations("-0.6 - -0.6 = 0", "-0.6 − ");
        testCalculations("-0.000005 - -0.0000000000005 = -0.0000049999995", "-0.000005 − ");
        testCalculations("-0.1 - -0.1 = 0", "-0.1 − ");
        testCalculations("-1000000 - -50000000 = 49,000,000", "-1000000 − ");
        testCalculations("-1000000 - -50000000.5 = 49,000,000.5", "-1000000 − ");
        testCalculations("-9999999999999999 - -9999999999999999 = 0", "-9999999999999999 − ");

        // with more than one operation in sequence
        testCalculations("-2 - 3 + 5 = 0", "-2 − 3 + ");
        testCalculations("0 - 0 + 0 = 0", "0 − 0 + ");
        testCalculations("0 - 5 / 500 = -0.01", "0 − 5 ÷ ");
        testCalculations("2 - 3 + 5 = 4", "2 − 3 + ");
        testCalculations("5000 - -6 + 50 = 5,056", "5000 − -6 + ");
        testCalculations("999999999999999 - 1 * 2 = 1,999,999,999,999,996", "999999999999999 − 1 × ");
        testCalculations("9999999999999999 - 1 / 2 = 4,999,999,999,999,999", "9999999999999999 − 1 ÷ ");
    }

    @Test
    public void testMultiplyOperation() {
        // with zero arguments
        testCalculations("0 * 555000000 = 0", "0 × ");
        testCalculations("0 * 1000 = 0", "0 × ");
        testCalculations("0 * 100 = 0", "0 × ");
        testCalculations("0 * 0.6666666666666667 = 0", "0 × ");
        testCalculations("0 * 0 = 0", "0 × ");
        testCalculations("0 * -0.6666666666666667 = 0", "0 × ");
        testCalculations("0 * -100 = 0", "0 × ");
        testCalculations("0 * -1000 = 0", "0 × ");
        testCalculations("0 * -555000000 = 0", "0 × ");

        // positive argument on positive
        testCalculations("1000000.5 * 50000000 = 50,000,025,000,000", "1000000.5 × ");
        testCalculations("1000000 * 50000000 = 50,000,000,000,000", "1000000 × ");
        testCalculations("100 * 50 = 5,000", "100 × ");
        testCalculations("5 * 55550 = 277,750", "5 × ");
        testCalculations("5 * 5 = 25", "5 × ");
        testCalculations("0.6666666666666667 * 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 × ");
        testCalculations("0.05 * 0.05 = 0.0025", "0.05 × ");
        testCalculations("0.000005 * 0.0000000000005 = 2.5e-18", "0.000005 × ");
        testCalculations("0.000005 * 0.1 = 0.0000005", "0.000005 × ");

        // positive argument on negative
        testCalculations("1000000.5 * -50000000 = -50,000,025,000,000", "1000000.5 × ");
        testCalculations("1000000 * -50000000 = -50,000,000,000,000", "1000000 × ");
        testCalculations("100 * -50 = -5,000", "100 × ");
        testCalculations("5 * 55550 = 277,750", "5 × ");
        testCalculations("5 * -5 = -25", "5 × ");
        testCalculations("0.6666666666666667 * 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 × ");
        testCalculations("0.05 * -0.05 = -0.0025", "0.05 × ");
        testCalculations("0.000005 * -0.0000000000005 = -2.5e-18", "0.000005 × ");
        testCalculations("0.000005 * -0.1 = -0.0000005", "0.000005 × ");

        // negative on positive
        testCalculations("-5 * 55550 = -277,750", "-5 × ");
        testCalculations("-100 * 50 = -5,000", "-100 × ");
        testCalculations("-2147483648 * 2147483647 = -4.611686016279904e+18", "-2147483648 × ");
        testCalculations("-0.6 * 0.6 = -0.36", "-0.6 × ");
        testCalculations("-0.000005 * 0.0000000000005 = -2.5e-18", "-0.000005 × ");
        testCalculations("-0.1 * 0.1 = -0.01", "-0.1 × ");
        testCalculations("-1000000 * 50000000 = -50,000,000,000,000", "-1000000 × ");
        testCalculations("-1000000 * 50000000.5 = -50,000,000,500,000", "-1000000 × ");

        // negative argument on negative
        testCalculations("-5 * -55550 = 277,750", "-5 × ");
        testCalculations("-100 * -50 = 5,000", "-100 × ");
        testCalculations("-0.6 * -0.6 = 0.36", "-0.6 × ");
        testCalculations("-0.000005 * -0.0000000000005 = 2.5e-18", "-0.000005 × ");
        testCalculations("-0.1 * -0.1 = 0.01", "-0.1 × ");
        testCalculations("-1000000 * -50000000 = 50,000,000,000,000", "-1000000 × ");
        testCalculations("-1000000 * -50000000.5 = 50,000,000,500,000", "-1000000 × ");

        // with more than one operation in sequence
        testCalculations("-1000000000000000 / 3 * 3 = -1,000,000,000,000,000", "-1000000000000000 ÷ 3 × ");
        testCalculations("-1000 / 3 * 3 = -1,000", "-1000 ÷ 3 × ");
        testCalculations("0 / 50 - 0 = 0", "0 ÷ 50 − ");
        testCalculations("1 / 3 * 3 = 1", "1 ÷ 3 × ");
        testCalculations("10 / 3 * 3 = 10", "10 ÷ 3 × ");
        testCalculations("1000 / 3 * 3 = 1,000", "1000 ÷ 3 × ");
        testCalculations("5000 / -6 + 50 = -783.3333333333333", "5000 ÷ -6 + ");
    }

    @Test
    public void testDivideOperation() {
        // zero by non-zero number
        testCalculations("0 / 555000000 = 0", "0 ÷ ");
        testCalculations("0 / 1000 = 0", "0 ÷ ");
        testCalculations("0 / 100 = 0", "0 ÷ ");
        testCalculations("0 / 0.6666666666666667 = 0", "0 ÷ ");
        testCalculations("0 / -0.6666666666666667 = 0", "0 ÷ ");
        testCalculations("0 / -100 = 0", "0 ÷ ");
        testCalculations("0 / -1000 = 0", "0 ÷ ");
        testCalculations("0 / -555000000 = 0", "0 ÷ ");

        // positive number by any non-zero number
        testCalculations("9999999999999999 / 9999999999999999 = 1", "9999999999999999 ÷ ");
        testCalculations("9999999999999999 / -9999999999999999 = -1", "9999999999999999 ÷ ");
        testCalculations("1000000.5 / -50000000 = -0.02000001", "1000000.5 ÷ ");
        testCalculations("1000000 / -50000000 = -0.02", "1000000 ÷ ");
        testCalculations("100 / -50 = -2", "100 ÷ ");
        testCalculations("5 / 55550 = 9.000900090009001e-5", "5 ÷ ");
        testCalculations("5 / -5 = -1", "5 ÷ ");
        testCalculations("0.05 / -0.05 = -1", "0.05 ÷ ");
        testCalculations("0.000005 / 0.0000000000005 = 10,000,000", "0.000005 ÷ ");
        testCalculations("0.000005 / -0.0000000000005 = -10,000,000", "0.000005 ÷ ");

        // negative number by any non-zero
        testCalculations("-0.6 / -0.6 = 1", "-0.6 ÷ ");
        testCalculations("-0.000005 / -0.0000000000005 = 10,000,000", "-0.000005 ÷ ");
        testCalculations("-0.1 / -0.1 = 1", "-0.1 ÷ ");
        testCalculations("-5 / 55550 = -9.000900090009001e-5", "-5 ÷ ");
        testCalculations("-100 / 50 = -2", "-100 ÷ ");
        testCalculations("-1000000 / -50000000 = 0.02", "-1000000 ÷ ");
        testCalculations("-1000000 / -50000000.5 = 0.0199999998", "-1000000 ÷ ");
        testCalculations("-2147483648 / 2147483647 = -1.000000000465661", "-2147483648 ÷ ");
        testCalculations("-9999999999999999 / -9999999999999999 = 1", "-9999999999999999 ÷ ");

        // with more than one operation in sequence
        testCalculations("-859999 * 66 + 9999999 = -46,759,935", "-859999 × 66 + ");
        testCalculations("-1000 * 3 / 3 = -1,000", "-1000 × 3 ÷ ");
        testCalculations("-2 * 3 + 5 = -1", "-2 × 3 + ");
        testCalculations("0 * 0 - 1500 = -1,500", "0 × 0 − ");
        testCalculations("2 * 3 + 5 = 11", "2 × 3 + ");
        testCalculations("90 * 22222 / -50 = -39,999.6", "90 × 22222 ÷ ");
        testCalculations("9999999999999999 * 9999999999999999 * 5 = 4.999999999999999e+32", "9999999999999999 × 9999999999999999 × ");
    }

    @Test
    public void testPercentOperation() {
        // percentage for base zero and any percent
        testCalculations("0 + 555000000 % = 0", "0 + 0 ");
        testCalculations("0 + 1000 % = 0", "0 + 0 ");
        testCalculations("0 + 100 % = 0", "0 + 0 ");
        testCalculations("0 + 0.6666666666666667 % = 0", "0 + 0 ");
        testCalculations("0 + 0 % = 0", "0 + 0 ");
        testCalculations("0 + -0.6666666666666667 % = 0", "0 + 0 ");
        testCalculations("0 + -100 % = 0", "0 + 0 ");
        testCalculations("0 + -1000 % = 0", "0 + 0 ");
        testCalculations("0 + -555000000 % = 0", "0 + 0 ");

        // percentage for positive base
        testCalculations("1000000.5 + -50000000 % = -499,999,249,999.5", "1000000.5 + -500000250000 ");
        testCalculations("1000000 + -50000000 % = -499,999,000,000", "1000000 + -500000000000 ");
        testCalculations("100 + -50 % = 50", "100 + -50 ");
        testCalculations("5 + 55550 % = 2,782.5", "5 + 2777.5 ");
        testCalculations("5 + -5 % = 4.75", "5 + -0.25 ");
        testCalculations("0.6666666666666667 + 0.0000000000000003 % = 0.6666666666666667", "0.6666666666666667 + 2.e-18 ");
        testCalculations("0.6666666666666667 + 0 % = 0.6666666666666667", "0.6666666666666667 + 0 ");
        testCalculations("0.05 + -0.05 % = 0.049975", "0.05 + -0.000025 ");
        testCalculations("0.000005 + 0.0000000000005 % = 5.000000000000025e-6", "0.000005 + 2.5e-20");
        testCalculations("0.000005 + -0.0000000000005 % = 4.999999999999975e-6", "0.000005 + -2.5e-20");

        // percentage for negative base
        testCalculations("-0.6 + -0.6 % = -0.5964", "-0.6 + 0.0036 ");
        testCalculations("-0.000005 + -0.0000000000005 % = -4.999999999999975e-6", "-0.000005 + 2.5e-20 ");
        testCalculations("-0.1 + -0.1 % = -0.0999", "-0.1 + 0.0001 ");
        testCalculations("-5 + 55550 % = -2,782.5", "-5 + -2777.5 ");
        testCalculations("-5 + 0 % = -5", "-5 + 0 ");
        testCalculations("-100 + 50 % = -150", "-100 + -50 ");
        testCalculations("-1000000 + -50000000 % = 499,999,000,000", "-1000000 + 500000000000 ");
        testCalculations("-1000000 + -50000000.5 % = 499,999,005,000", "-1000000 + 500000005000 ");
    }

    @Test
    public void testNegateOperation() {
        // with positive argument
        testCalculations("± 9999999999999999 = -9,999,999,999,999,999", "");
        testCalculations("± 1000000.5 = -1,000,000.5", "");
        testCalculations("± 1000 = -1,000", "");
        testCalculations("± 100 = -100", "");
        testCalculations("± 0.6666666666666667 = -0.6666666666666667", "");
        testCalculations("± 0.05 = -0.05", "");
        testCalculations("± 0.0000000000005 = -0.0000000000005", "");

        // with zero
        testCalculations("± 0 = 0", "");

        // with negative argument
        testCalculations("± -0.0000000000005 = 0.0000000000005", "");
        testCalculations("± -0.05 = 0.05", "");
        testCalculations("± -0.6666666666666667 = 0.6666666666666667", "");
        testCalculations("± -100 = 100", "");
        testCalculations("± -1000 = 1,000", "");
        testCalculations("± -555000000 = 555,000,000", "");
        testCalculations("± -9999999999999999 = 9,999,999,999,999,999", "");

        // with more than one unary operations for one number
        testCalculations("± √ 0 = 0", "√(0)");
        testCalculations("± sqr 700 = 490,000", "sqr(-700)");
        testCalculations("± sqr 50 = 2,500", "sqr(-50)");
        testCalculations("± ± 55 = 55", "");
        testCalculations("± 1/ 9999999999999999 = -0.0000000000000001", "1/(-9999999999999999) ");
    }

    @Test
    public void testSquareOperation() {
        // with positive argument
        testCalculations("sqr 1000000.5 = 1,000,001,000,000.25", "sqr(1000000.5)");
        testCalculations("sqr 1000 = 1,000,000", "sqr(1000)");
        testCalculations("sqr 100 = 10,000", "sqr(100)");
        testCalculations("sqr 0.6666666666666667 = 0.4444444444444445", "sqr(0.6666666666666667)");
        testCalculations("sqr 0.05 = 0.0025", "sqr(0.05)");
        testCalculations("sqr 0.0000000000005 = 2.5e-25", "sqr(0.0000000000005)");

        // with zero
        testCalculations("sqr 0 = 0", "sqr(0)");

        // with negative argument
        testCalculations("sqr -0.0000000000005 = 2.5e-25", "sqr(-0.0000000000005)");
        testCalculations("sqr -0.05 = 0.0025", "sqr(-0.05)");
        testCalculations("sqr -0.6666666666666667 = 0.4444444444444445", "sqr(-0.6666666666666667)");
        testCalculations("sqr -100 = 10,000", "sqr(-100)");
        testCalculations("sqr -1000 = 1,000,000", "sqr(-1000)");
        testCalculations("sqr -555000000 = 3.08025e+17", "sqr(-555000000)");

        // with more than one unary operations for one number
        testCalculations("sqr sqr 0 = 0", "sqr(sqr(0))");
        testCalculations("sqr sqr 0.005 = 0.000000000625", "sqr(sqr(0.005))");
        testCalculations("sqr √ 700 = 700", "√(sqr(700))");
        testCalculations("sqr 1/ 55 = 3.305785123966942e-4", "1/(sqr(55))");
        testCalculations("sqr sqr 9999999999999999 = 9.999999999999996e+63", "sqr(sqr(9999999999999999))");
        testCalculations("sqr ± 55 = -3,025", "sqr(55)");
    }

    @Test
    public void testSquareRootOperation() {
        testCalculations("√ 9999999999999999 = 100,000,000", "√(9999999999999999)");
        testCalculations("√ 58585858585 = 242,045.158152358", "√(58585858585)");
        testCalculations("√ 1000000.5 = 1,000.000249999969", "√(1000000.5)");
        testCalculations("√ 1000 = 31.62277660168379", "√(1000)");
        testCalculations("√ 100 = 10", "√(100)");
        testCalculations("√ 0.6666666666666667 = 0.8164965809277261", "√(0.6666666666666667)");
        testCalculations("√ 0.05 = 0.223606797749979", "√(0.05)");
        testCalculations("√ 0.0000000000005 = 7.071067811865475e-7", "√(0.0000000000005)");
        testCalculations("√ 0 = 0", "√(0)");

        // with more than one unary operations for one number
        testCalculations("√ √ 0 = 0", "√(√(0))");
        testCalculations("√ sqr 700 = 700", "sqr(√(700))");
        testCalculations("√ √ 50 = 2.659147948472494", "√(√(50))");
        testCalculations("√ ± 55 = -7.416198487095663", "√(55)");
        testCalculations("√ 1/ 9999999999999999 = 0.00000001", "1/(√(9999999999999999))");
    }

    @Test
    public void testReverseOperation() {
        // positive argument
        testCalculations("1/ 9999999999999999 = 0.0000000000000001", "1/(9999999999999999)");
        testCalculations("1/ 555000000 = 1.801801801801802e-9", "1/(555000000)");
        testCalculations("1/ 1000 = 0.001", "1/(1000)");
        testCalculations("1/ 100 = 0.01", "1/(100)");
        testCalculations("1/ 0.6666666666666667 = 1.5", "1/(0.6666666666666667)");
        testCalculations("1/ 0.05 = 20", "1/(0.05)");

        // negative argument
        testCalculations("1/ -0.05 = -20", "1/(-0.05)");
        testCalculations("1/ -0.6666666666666667 = -1.5", "1/(-0.6666666666666667)");
        testCalculations("1/ -100 = -0.01", "1/(-100)");
        testCalculations("1/ -1000 = -0.001", "1/(-1000)");
        testCalculations("1/ -555000000 = -1.801801801801802e-9", "1/(-555000000)");
        testCalculations("1/ -9999999999999999 = -0.0000000000000001", "1/(-9999999999999999)");

        // with more than one unary operations for one number
        testCalculations("1/ 1/ 5 = 5", "1/(1/(5))");
        testCalculations("1/ 1/ -5 = -5", "1/(1/(-5))");
        testCalculations("1/ ± -5 = 0.2", "1/(-5)");
        testCalculations("1/ √ 35 = 0.1690308509457034", "√(1/(35))");
        testCalculations("1/ sqr -5 = 0.04", "sqr(1/(-5))");
    }

    private void testCalculations(String expression, String expectedHistory) {
        pushKey(ESCAPE);
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int lastElementIndex = expressionParts.length - 1;
        String expectedResult = expressionParts[lastElementIndex];
        String firstArgument = expressionParts[0];

        String[] argumentsWithoutResult = Arrays.copyOf(expressionParts, lastElementIndex);
        if (isNumber(firstArgument)) {
            performBinaryCalculations(argumentsWithoutResult);
        } else {
            performUnaryOperations(argumentsWithoutResult);
        }
        testHistory(expectedHistory);
        testCalculation(expectedResult);
    }

    private void performBinaryCalculations(String[] expressionParts) {
        for (int i = 0; i < expressionParts.length; i++) {
            processElement(expressionParts[i]);
        }
    }

    private void processElement(String expressionPart) {
        WaitForAsyncUtils.waitForFxEvents();

        if (isNumber(expressionPart)) {
            enterNumber(expressionPart);
        } else {
            executeMathOperation(expressionPart);
        }
    }

    private void executeMathOperation(String operationSymbol) {
        MathOperation operation = extractOperation(operationSymbol);
        fireButton(operation.id());
    }

    private void performUnaryOperations(String[] expressionParts) {
        int lastElementIndex = expressionParts.length - 1;
        String baseNumber = expressionParts[lastElementIndex];
        enterNumber(baseNumber);

        // execute all unary operations
        for (int i = 0; i < lastElementIndex; i++) {
            executeMathOperation(expressionParts[i]);
        }
    }

    private void enterNumber(String number) {
        int i = 0;
        boolean isNegativeNumber = number.startsWith(MINUS);
        if (isNegativeNumber) { // skip minus
            i++;
        }
        for (; i < number.length(); i++) { // add digit or point
            addDigit(number.charAt(i));
        }
        if (isNegativeNumber) { // add minus
            fireButton(NEGATE.id());
        }
    }

    private void addDigit(char digit) {
        String digitStr = String.valueOf(digit);
        if (POINT.equals(digitStr)) {
            pushKey(KeyCode.PERIOD);
        } else {
            pushKey(getKeyCode(digitStr));
        }
    }

    public static MathOperation extractOperation(String operation) {
        switch (operation) {
            case "1/":
                return REVERSE;
            case "sqr":
                return SQUARE;
            case "√":
                return SQUARE_ROOT;
            case "±":
                return NEGATE;
            case "*":
                return MULTIPLY;
            case "/":
                return DIVIDE;
            case "-":
                return SUBTRACT;
            case "+":
                return ADD;
            case "%":
                return PERCENT;
            default:
                return MathOperation.getOperation(operation);
        }
    }

    @Test
    public void testUnaryAfterBinary() {
        testUnaryAfterBinary("5 + 1/ 9999999999999999 = 5", "5 + 1/(9999999999999999)");
        testUnaryAfterBinary("5 + 1/ 0.0000000000000001 = 1.000000000000001e+16", "5 + 1/(0.0000000000000001)");

        testUnaryAfterBinary("5 + sqr 9999999999999999 = 9.999999999999998e+31", "5 + sqr(9999999999999999)");
        testUnaryAfterBinary("5 + sqr 0.0000000000000001 = 5", "5 + sqr(0.0000000000000001)");

        testUnaryAfterBinary("5 + √ 9999999999999999 = 100,000,005", "5 + √(9999999999999999)");
        testUnaryAfterBinary("5 + √ 0.0000000000000001 = 5.00000001", "5 + √(0.0000000000000001)");
    }

    private void testUnaryAfterBinary(String expression, String expectedHistory) {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int lastElementIndex = expressionParts.length - 1;
        String expectedResult = expressionParts[lastElementIndex];

        performBinaryCalculations(Arrays.copyOf(expressionParts, 2));
        performUnaryOperations(Arrays.copyOfRange(expressionParts, 2, lastElementIndex));

        WaitForAsyncUtils.waitForFxEvents();
        testHistory(expectedHistory);
        testCalculation(expectedResult);
    }

    @Test
    public void testMemoryOperations() {
        // add to memorized value
        testMemoryOperation("M+ 50 = 50");
        testMemoryOperation("M+ -3 = 47");
        testMemoryOperation("M+ 0.555555 = 47.555555");
        testMemoryOperation("M+ -99999999999 = -99,999,999,951.44445");
        testMemoryOperation("M+ 0 = -99,999,999,951.44445");
        testMemoryOperation("M+ 9999999999999999 = 9,999,900,000,000,048");

        fireButton(MEMORY_CLEAN.symbol());

        // subtract from memorized value
        testMemoryOperation("M- 50 = -50");
        testMemoryOperation("M- -3 = -47");
        testMemoryOperation("M- 0.555555 = -47.555555");
        testMemoryOperation("M- -99999999999 = 99,999,999,951.44445");
        testMemoryOperation("M- 0 = 99,999,999,951.44445");
        testMemoryOperation("M- 9999999999999999 = -9,999,900,000,000,048");
        fireButton(MEMORY_CLEAN.symbol());

        // store value in memorized
        testMemoryOperation("MS 50 = 50");
        testMemoryOperation("MS -3 = -3");
        testMemoryOperation("MS 0.555555 = 0.555555");
        testMemoryOperation("MS -99999999999 = -99,999,999,999");
        testMemoryOperation("MS 0 = 0");
        testMemoryOperation("MS 9999999999999999 = 9,999,999,999,999,999");
        fireButton(MEMORY_CLEAN.symbol());
    }

    private void testMemoryOperation(String expression) {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;
        // enter new number and execute memory operation
        String operation = expressionParts[index++];
        String number = expressionParts[index++];
        enterNumber(number);
        fireButton(operation);
        testHistory("");

        //reset entered number and check for 0 (default value for new number)
        fireButton(CLEAN_CURRENT.id());
        testCurrentText("0");

        // recall memorized value
        fireButton(MEMORY_RECALL.symbol());

        WaitForAsyncUtils.waitForFxEvents();
        String expectedMemorizedValue = expressionParts[index];
        testCurrentText(expectedMemorizedValue);
    }

    @Test
    public void testDisableMemoryButtons() {
        // Memory operations that activates all memory buttons
        testDisablingMemoryButtons(MEMORY_ADD);
        testDisablingMemoryButtons(MEMORY_SUBTRACT);
        testDisablingMemoryButtons(MEMORY_STORE);
    }

    private void testDisablingMemoryButtons(MemoryOperation operation) {
        // Memory clean, recall and show are disabled by default
        testIsDisableButton(MEMORY_CLEAN.symbol(), true);
        testIsDisableButton(MEMORY_RECALL.symbol(), true);
        testIsDisableButton(MEMORY_SHOW.symbol(), true);
        // Memory add, subtract and store are active by default
        testIsDisableButton(MEMORY_ADD.symbol(), false);
        testIsDisableButton(MEMORY_SUBTRACT.symbol(), false);
        testIsDisableButton(MEMORY_STORE.symbol(), false);

        fireButton(operation.symbol());

        // After pressing any memory button, all memory buttons become active
        testIsDisableButton(MEMORY_CLEAN.symbol(), false);
        testIsDisableButton(MEMORY_RECALL.symbol(), false);
        testIsDisableButton(MEMORY_SHOW.symbol(), false);
        testIsDisableButton(MEMORY_ADD.symbol(), false);
        testIsDisableButton(MEMORY_SUBTRACT.symbol(), false);
        testIsDisableButton(MEMORY_STORE.symbol(), false);
        // reset everything
        fireButton(MEMORY_CLEAN.symbol());
    }

    private void testIsDisableButton(String buttonId, boolean expectedDisabled) {
        Button memoryButton = buttons.get(buttonId);
        boolean isButtonDisabled = memoryButton.isDisabled();
        assertEquals(expectedDisabled, isButtonDisabled);
    }

    @Test
    public void testOperationWithWrongArguments() {

        // division by zero
        testOperationForException("555000000 / 0 = Cannot divide by zero", "555000000 ÷ ");
        testOperationForException("1000 / 0 = Cannot divide by zero", "1000 ÷ ");
        testOperationForException("100 / 0 = Cannot divide by zero", "100 ÷ ");
        testOperationForException("0.6666666666666667 / 0 = Cannot divide by zero", "0.6666666666666667 ÷ ");
        testOperationForException("0 / 0 = Result is undefined", "0 ÷ ");
        testOperationForException("0 / 0.0 = Result is undefined", "0 ÷ ");
        testOperationForException("-0.6666666666666667 / 0 = Cannot divide by zero", "-0.6666666666666667 ÷ ");
        testOperationForException("-100 / 0 = Cannot divide by zero", "-100 ÷ ");
        testOperationForException("-1000 / 0 = Cannot divide by zero", "-1000 ÷ ");
        testOperationForException("-555000000 / 0 = Cannot divide by zero", "-555000000 ÷ ");

        // square root with negative argument
        testOperationForException("√ -5 = Invalid input", "√(-5)");
        testOperationForException("√ -5.5 = Invalid input", "√(-5.5)");
        testOperationForException("√ -10000000 = Invalid input", "√(-10000000)");
        testOperationForException("√ -2147483648 = Invalid input", "√(-2147483648)");
        testOperationForException("√ -555555555000005 = Invalid input", "√(-555555555000005)");
        testOperationForException("√ -9223372036854775 = Invalid input", "√(-9223372036854775)");
        testOperationForException("√ -9999999999999999 = Invalid input", "√(-9999999999999999)");

        // reverse with zero base
        testOperationForException("1/ 0 = Cannot divide by zero", "1/(0)");
        testOperationForException("1/ 0.0 = Cannot divide by zero", "1/(0)");
    }

    private void testOperationForException(String expression, String expectedHistory) {
        pushKey(KeyCode.ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String[] expressionParts = expression.substring(0, equalSignIndex).trim().split(ARGUMENT_DELIMITERS);
        String expectedErrorMessage = expression.substring(equalSignIndex + 1).trim();

        if (expressionParts.length == 2) { // Expression for unary operations has format: operation baseNumber
            performUnaryOperations(expressionParts);

        } else { // Expression for binary operations has format: firstNumber operation secondNumber
            performBinaryCalculations(expressionParts);
            pushKey(KeyCode.ENTER);
        }

        testCurrentText(expectedErrorMessage);
        testHistory(expectedHistory);
    }

    @Test
    public void testPressDisabledButtons() {
        // cause an exception by dividing zero by zero to disable buttons with operations
        testOperationForException("0 / 0 = Result is undefined", "0 ÷ ");

        for (MathOperation operation : MathOperation.values()) {
            if (operation != RESULT) {
                testIsDisableButton(operation.id(), true);
            }
        }
        for (MemoryOperation operation : MemoryOperation.values()) {
            testIsDisableButton(operation.symbol(), true);
        }
        testIsDisableButton(POINT, true);
        testIsDisableButton("history", true);

        // try to press math operation from keyboard
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.EQUALS, SHIFT_DOWN); // +
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.MINUS);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIVIDE);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT8, SHIFT_DOWN); // *
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT2, SHIFT_DOWN); // √
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT5, SHIFT_DOWN); // %

        // clean error message
        pushKey(KeyCode.ESCAPE);
        testCurrentText("0");
    }

    @Test
    public void testForOverflow() {
        // result is overflow
        testForOverflow("1.e-9990 / 10000000000 = Overflow");
        testForOverflow("1.e-9990 / 100000000000 = Overflow");
        testForOverflow("1.e-9990 / 1000000000000 = Overflow");
        testForOverflow("1.e-9990 / 10000000000000 = Overflow");
        testForOverflow("1.e-9990 / 100000000000000 = Overflow");

        testForOverflow("1.e+9990 * 10000000000 = Overflow");
        testForOverflow("1.e+9990 * 100000000000 = Overflow");
        testForOverflow("1.e+9990 * 1000000000000 = Overflow");
        testForOverflow("1.e+9990 * 10000000000000 = Overflow");
        testForOverflow("1.e+9990 * 100000000000000 = Overflow");
    }

    private void testForOverflow(String expression) {
        pushKey(KeyCode.ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String[] expressionParts = expression.substring(0, equalSignIndex).trim().split(ARGUMENT_DELIMITERS);
        String expectedErrorMessage = expression.substring(equalSignIndex + 1).trim();

        enterNumber("1");
        MathOperation operation = extractOperation(expressionParts[1]);
        fireButton(operation.id());
        enterNumber("1000000000000000");

        String firstNumber = expressionParts[0];

        // press result button many times to get too large or too small number
        while (!currentNumberText.getText().trim().equals(firstNumber.trim())) {
            pushKey(KeyCode.ENTER);
        }

        fireButton(operation.id());
        String secondNumber = expressionParts[2];
        enterNumber(secondNumber);

        testCalculation(expectedErrorMessage);
    }

    @Test
    public void testInitWithWrongArgumentCount() {
        testInitForException(null, 0);
        testInitForException(null, 1);

        // binary operation with wrong argument count
        testInitForException(ADD, 0);
        testInitForException(ADD, 1);
        testInitForException(ADD, 3);
        testInitForException(ADD, 4);
        testInitForException(ADD, 10);

        // unary operation with wrong argument count
        testInitForException(SQUARE_ROOT, 0);
        testInitForException(SQUARE_ROOT, 2);
        testInitForException(SQUARE_ROOT, 3);
        testInitForException(SQUARE_ROOT, 4);
        testInitForException(SQUARE_ROOT, 10);
    }

    private void testInitForException(MathOperation operation, int argumentsCount) {
        try {
            new StandardCalculator(operation, new BigDecimal[argumentsCount]);
            fail(format("Expected CalculationException with wrong arguments. Your operation is %s, count of arguments is %d",
                    operation, argumentsCount));
        } catch (CalculationException e) {
            // expected
        }
    }

    private void testCurrentText(String expected) {
        assertEquals(expected, currentNumberText.getText());
    }

    private void fireButton(String buttonId) {
        WaitForAsyncUtils.waitForFxEvents();
        final Button button = buttons.get(buttonId);
        button.fire();
    }

    private void testHistory(String expected) {
        assertEquals(expected.trim(), prevOperationsText.getText().trim());
    }

    private void testCalculation(String expectedResult) {
        pushKey(KeyCode.ENTER);
        testCurrentText(expectedResult);
    }

    private void testKeyPressed(String expectedText, KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        pushKey(keyCode, modifiers);
        testCurrentText(expectedText);
    }

    private void pushKey(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        KeyCodeCombination combination = new KeyCodeCombination(keyCode, modifiers);
        robot.push(combination);
    }
}