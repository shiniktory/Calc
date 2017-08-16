package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
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
            addButton(operation.symbol(), operation.id());
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
        testCalculations("9999999999999999 - 1 = 9,999,999,999,999,998", "9999999999999999 − ");
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
        testCalculations("9999999999999999 * 2 = 2.e+16", "9999999999999999 × ");
        testCalculations("1000000.5 * 50000000 = 50,000,025,000,000", "1000000.5 × ");
        testCalculations("1000000 * 50000000 = 50,000,000,000,000", "1000000 × ");
        testCalculations("100 * 50 = 5,000", "100 × ");
        testCalculations("5 * 55550 = 277,750", "5 × ");
        testCalculations("5 * 5 = 25", "5 × ");
        testCalculations("0.6666666666666667 * 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 × ");
        testCalculations("0.05 * 0.05 = 0.0025", "0.05 × ");
        testCalculations("0.000005 * 0.1 = 0.0000005", "0.000005 × ");

        // positive argument on negative
        testCalculations("1000000.5 * -50000000 = -50,000,025,000,000", "1000000.5 × ");
        testCalculations("1000000 * -50000000 = -50,000,000,000,000", "1000000 × ");
        testCalculations("100 * -50 = -5,000", "100 × ");
        testCalculations("5 * 55550 = 277,750", "5 × ");
        testCalculations("5 * -5 = -25", "5 × ");
        testCalculations("0.6666666666666667 * 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 × ");
        testCalculations("0.05 * -0.05 = -0.0025", "0.05 × ");
        testCalculations("0.000005 * -0.1 = -0.0000005", "0.000005 × ");

        // negative on positive
        testCalculations("-5 * 55550 = -277,750", "-5 × ");
        testCalculations("-100 * 50 = -5,000", "-100 × ");
        testCalculations("-0.6 * 0.6 = -0.36", "-0.6 × ");
        testCalculations("-0.1 * 0.1 = -0.01", "-0.1 × ");
        testCalculations("-1000000 * 50000000 = -50,000,000,000,000", "-1000000 × ");
        testCalculations("-1000000 * 50000000.5 = -50,000,000,500,000", "-1000000 × ");

        // negative argument on negative
        testCalculations("-5 * -55550 = 277,750", "-5 × ");
        testCalculations("-100 * -50 = 5,000", "-100 × ");
        testCalculations("-0.6 * -0.6 = 0.36", "-0.6 × ");
        testCalculations("-0.1 * -0.1 = 0.01", "-0.1 × ");
        testCalculations("-1000000 * -50000000 = 50,000,000,000,000", "-1000000 × ");
        testCalculations("-1000000 * -50000000.5 = 50,000,000,500,000", "-1000000 × ");

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
        testCalculations("5 / -5 = -1", "5 ÷ ");
        testCalculations("0.05 / -0.05 = -1", "0.05 ÷ ");
        testCalculations("0.000005 / 0.0000000000005 = 10,000,000", "0.000005 ÷ ");
        testCalculations("0.000005 / -0.0000000000005 = -10,000,000", "0.000005 ÷ ");

        // negative number by any non-zero
        testCalculations("-0.000005 / -0.0000000000005 = 10,000,000", "-0.000005 ÷ ");
        testCalculations("-0.1 / -0.1 = 1", "-0.1 ÷ ");
        testCalculations("-0.6 / -0.6 = 1", "-0.6 ÷ ");
        testCalculations("-100 / 50 = -2", "-100 ÷ ");
        testCalculations("-1000000 / -50000000 = 0.02", "-1000000 ÷ ");
        testCalculations("-1000000 / -50000000.5 = 0.0199999998", "-1000000 ÷ ");
        testCalculations("-2147483648 / 2147483647 = -1.000000000465661", "-2147483648 ÷ ");
        testCalculations("-9999999999999999 / -9999999999999999 = 1", "-9999999999999999 ÷ ");

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
        testCalculations("9999999999999999 ± = -9,999,999,999,999,999", "");
        testCalculations("1000000.5 ± = -1,000,000.5", "");
        testCalculations("1000 ± = -1,000", "");
        testCalculations("100 ± = -100", "");
        testCalculations("0.6666666666666667 ± = -0.6666666666666667", "");
        testCalculations("0.05 ± = -0.05", "");
        testCalculations("0.0000000000005 ± = -0.0000000000005", "");

        // with zero
        testCalculations("0 ± = 0", "");

        // with negative argument
        testCalculations("-0.0000000000005 ± = 0.0000000000005", "");
        testCalculations("-0.05 ± = 0.05", "");
        testCalculations("-0.6666666666666667 ± = 0.6666666666666667", "");
        testCalculations("-100 ± = 100", "");
        testCalculations("-1000 ± = 1,000", "");
        testCalculations("-555000000 ± = 555,000,000", "");
        testCalculations("-9999999999999999 ± = 9,999,999,999,999,999", "");

        // with more than one unary operations for one number
        testCalculations("0 ± √ = 0", "√(0)");
        testCalculations("700 ± sqr = 490,000", "sqr(-700)");
        testCalculations("50 ± sqr = 2,500", "sqr(-50)");
        testCalculations("55 ± ± = 55", "");
        testCalculations("9999999999999999 ± 1/ = -0.0000000000000001", "1/(-9999999999999999) ");
    }

    @Test
    public void testSquareOperation() {
        // with positive argument
        testCalculations("1000000.5 sqr = 1,000,001,000,000.25", "sqr(1000000.5)");
        testCalculations("1000 sqr = 1,000,000", "sqr(1000)");
        testCalculations("100 sqr = 10,000", "sqr(100)");
        testCalculations("0.6666666666666667 sqr = 0.4444444444444445", "sqr(0.6666666666666667)");
        testCalculations("0.05 sqr = 0.0025", "sqr(0.05)");

        // with zero
        testCalculations("0 sqr = 0", "sqr(0)");

        // with negative argument
        testCalculations("-0.05 sqr = 0.0025", "sqr(-0.05)");
        testCalculations("-0.6666666666666667 sqr = 0.4444444444444445", "sqr(-0.6666666666666667)");
        testCalculations("-100 sqr = 10,000", "sqr(-100)");
        testCalculations("-1000 sqr = 1,000,000", "sqr(-1000)");

        // with more than one unary operations for one number
        testCalculations("0 sqr sqr = 0", "sqr(sqr(0))");
        testCalculations("0.005 sqr sqr = 0.000000000625", "sqr(sqr(0.005))");
        testCalculations("700 sqr √ = 700", "√(sqr(700))");
        testCalculations("55 sqr ± = -3,025", "sqr(55)");
    }

    @Test
    public void testSquareRootOperation() {
        testCalculations("9999999999999999 √ = 100,000,000", "√(9999999999999999)");
        testCalculations("58585858585 √ = 242,045.158152358", "√(58585858585)");
        testCalculations("1000000.5 √ = 1,000.000249999969", "√(1000000.5)");
        testCalculations("1000 √ = 31.62277660168379", "√(1000)");
        testCalculations("100 √ = 10", "√(100)");
        testCalculations("0.6666666666666667 √ = 0.8164965809277261", "√(0.6666666666666667)");
        testCalculations("0.05 √ = 0.223606797749979", "√(0.05)");
        testCalculations("0 √ = 0", "√(0)");

        // with more than one unary operations for one number
        testCalculations("0 √ √ = 0", "√(√(0))");
        testCalculations("700 √ sqr = 700", "sqr(√(700))");
        testCalculations("50 √ √ = 2.659147948472494", "√(√(50))");
        testCalculations("55 √ ± = -7.416198487095663", "√(55)");
        testCalculations("9999999999999999 √ 1/ = 0.00000001", "1/(√(9999999999999999))");
    }

    @Test
    public void testReverseOperation() {
        // positive argument
        testCalculations("9999999999999999 1/ = 0.0000000000000001", "1/(9999999999999999)");
        testCalculations("1000 1/ = 0.001", "1/(1000)");
        testCalculations("100 1/ = 0.01", "1/(100)");
        testCalculations("0.6666666666666667 1/ = 1.5", "1/(0.6666666666666667)");
        testCalculations("0.05 1/ = 20", "1/(0.05)");

        // negative argument
        testCalculations("-0.05 1/ = -20", "1/(-0.05)");
        testCalculations("-0.6666666666666667 1/ = -1.5", "1/(-0.6666666666666667)");
        testCalculations("-100 1/ = -0.01", "1/(-100)");
        testCalculations("-1000 1/ = -0.001", "1/(-1000)");
        testCalculations("-9999999999999999 1/ = -0.0000000000000001", "1/(-9999999999999999)");

        // with more than one unary operations for one number
        testCalculations("5 1/ 1/ = 5", "1/(1/(5))");
        testCalculations("-5 1/ 1/ = -5", "1/(1/(-5))");
        testCalculations("-5 1/ ± = 0.2", "1/(-5)");
        testCalculations("35 1/ √ = 0.1690308509457034", "√(1/(35))");
        testCalculations("-5 1/ sqr = 0.04", "sqr(1/(-5))");
    }

    @Test
    public void testUnaryAfterBinary() {
        testCalculations("5 + 9999999999999999 1/ = 5", "5 + 1/(9999999999999999)");
        testCalculations("5 + 9999999999999999 1/ sqr = 5", "5 + sqr(1/(9999999999999999))");
        testCalculations("5 + 0.0000000000000001 1/ = 1.000000000000001e+16", "5 + 1/(0.0000000000000001)");
        testCalculations("5 + 0.0000000000000001 ± 1/ = -9,999,999,999,999,995", "5 + 1/(-0.0000000000000001)");

        testCalculations("5 + 9999999999999999 sqr = 9.999999999999998e+31", "5 + sqr(9999999999999999)");
        testCalculations("5 + 9999999999999999 sqr √ = 1.e+16", "5 + √(sqr(9999999999999999))");
        testCalculations("5 + 0.0000000000000001 sqr = 5", "5 + sqr(0.0000000000000001)");
        testCalculations("5 + 0.0000000000000001 sqr 1/ = 1.e+32", "5 + 1/(sqr(0.0000000000000001))");

        testCalculations("5 + 9999999999999999 √ = 100,000,005", "5 + √(9999999999999999)");
        testCalculations("5 + 9999999999999999 √ ± = -99,999,995", "5 + √(9999999999999999)");
        testCalculations("5 + 0.0000000000000001 √ = 5.00000001", "5 + √(0.0000000000000001)");
        testCalculations("5 + 0.0000000000000001 √ 1/ = 100,000,005", "5 + 1/(√(0.0000000000000001))");
    }

    @Test
    public void testExponentialView() {

        // POSITIVE EXPONENT for numbers greater 9999999999999999
        // get maximum number without exponent
        testCalculations("9999999999999998 + 1 = 9,999,999,999,999,999", "9999999999999998 + ");
        testCalculations("9999999999999999 + 0.4 = 9,999,999,999,999,999", "9999999999999999 + ");
        testCalculations("-9999999999999998 + -1 = -9,999,999,999,999,999", "-9999999999999998 + ");
        testCalculations("-9999999999999999 + -0.4 = -9,999,999,999,999,999", "-9999999999999999 + ");

        // get number 10,000,000,000,000,000 or greater that must be converted to an exponential view
        testCalculations("-9999999999999999 + -0.5 = -1.e+16", "-9999999999999999 + ");
        testCalculations("-9999999999999999 + -1 = -1.e+16", "-9999999999999999 + ");
        testCalculations("-9999999999999999 - 1 = -1.e+16", "-9999999999999999 − ");
        testCalculations("-9999999999999999 + -2 = -1.e+16", "-9999999999999999 + ");
        testCalculations("9999999999999999 + 0.5 = 1.e+16", "9999999999999999 + ");
        testCalculations("9999999999999999 + 1 = 1.e+16", "9999999999999999 + ");
        testCalculations("9999999999999999 - -1 = 1.e+16", "9999999999999999 − ");
        testCalculations("9999999999999999 + 2 = 1.e+16", "9999999999999999 + ");
        testCalculations("-9999999999999999 * 2 = -2.e+16", "-9999999999999999 × ");
        testCalculations("-9999999999999999 + -9999999999999999 = -2.e+16", "-9999999999999999 + ");
        testCalculations("-9999999999999999 - 9999999999999999 = -2.e+16", "-9999999999999999 − ");
        testCalculations("9999999999999999 + 9999999999999999 = 2.e+16", "9999999999999999 + ");
        testCalculations("9999999999999999 - -9999999999999999 = 2.e+16", "9999999999999999 − ");
        testCalculations("-9999999999999999 * -2 = 2.e+16", "-9999999999999999 × ");
        testCalculations("-555000000 sqr = 3.08025e+17", "sqr(-555000000)");
        testCalculations("-2147483648 * 2147483647 = -4.611686016279904e+18", "-2147483648 × ");
        testCalculations("9999999999999999 sqr sqr = 9.999999999999996e+63", "sqr(sqr(9999999999999999))");


        // NEGATIVE EXPONENT for numbers less than 0.001 and are too long
        // get number 0.001 or too long numbers greater than 0.001 without exponential view
        testCalculations("0.001 / 10 = 0.0001", "0.001 ÷ ");
        testCalculations("0.01 / 3 = 0.0033333333333333", "0.01 ÷ ");
        testCalculations("0.011111111111111 / 10 = 0.0011111111111111", "0.011111111111111 ÷ ");
        testCalculations("0.099999999999998 / 10 = 0.0099999999999998", "0.099999999999998 ÷ ");

        // get numbers less than 0.001 and that are too long. Must be converted to exponential view
        testCalculations("0.0011111111111111 / 10 = 1.1111111111111e-4", "0.0011111111111111 ÷ ");
        testCalculations("0.001 / 3 = 3.333333333333333e-4", "0.001 ÷ ");
        testCalculations("-0.001 / -3 = 3.333333333333333e-4", "-0.001 ÷ ");
        testCalculations("55 sqr 1/ = 3.305785123966942e-4", "1/(sqr(55))");
        testCalculations("0.0000007 √ = 8.366600265340755e-4", "√(0.0000007)");
        testCalculations("0.0099999999999998 / 10 = 9.9999999999998e-4", "0.0099999999999998 ÷ ");
        testCalculations("-5 / 55550 = -9.000900090009001e-5", "-5 ÷ ");
        testCalculations("-55555 1/ = -1.800018000180002e-5", "1/(-55555)");
        testCalculations("55555 1/ = 1.800018000180002e-5", "1/(55555)");
        testCalculations("0.000000007 √ = 8.366600265340755e-5", "√(0.000000007)");
        testCalculations("5 / 55550 = 9.000900090009001e-5", "5 ÷ ");
        testCalculations("0.000000000007 √ = 2.645751311064591e-6", "√(0.000000000007)");
        testCalculations("0.000005 + -0.0000000000005 % = 4.999999999999975e-6", "0.000005 + -2.5e-20");
        testCalculations("0.000005 + 0.0000000000005 % = 5.000000000000025e-6", "0.000005 + 2.5e-20");
        testCalculations("0.0000000000005 √ = 7.071067811865475e-7", "√(0.0000000000005)");
        testCalculations("-555000000 1/ = -1.801801801801802e-9", "1/(-555000000)");
        testCalculations("555000000 1/ = 1.801801801801802e-9", "1/(555000000)");
        testCalculations("-5555555555 1/ = -1.80000000018e-10", "1/(-5555555555)");
        testCalculations("5555555555 1/ = 1.80000000018e-10", "1/(5555555555)");

        // NEGATIVE EXPONENT for numbers less than 0.0000000000000001
        // get minimum number (closest to zero) without exponential view
        testCalculations("0.000000000000001 / 10 = 0.0000000000000001", "0.000000000000001 ÷ ");
        testCalculations("0.000000000000001 - 0.0000000000000009 = 0.0000000000000001", "0.000000000000001 − ");
        testCalculations("-0.000000000000001 / 10 = -0.0000000000000001", "-0.000000000000001 ÷ ");
        testCalculations("-0.000000000000001 - -0.0000000000000009 = -0.0000000000000001", "-0.000000000000001 − ");

        // get number 0.00000000000000009 or less that must be converted to an exponential view
        testCalculations("-0.0000000000000001 * 0.1 = -1.e-17", "-0.0000000000000001 × ");
        testCalculations("0.1 / -9999999999999999 = -1.e-17", "0.1 ÷ ");
        testCalculations("0.1 / 9999999999999999 = 1.e-17", "0.1 ÷ ");
        testCalculations("-0.0000000000000001 * -0.1 = 1.e-17", "-0.0000000000000001 × ");
        testCalculations("0.0000000000000001 * 0.1 = 1.e-17", "0.0000000000000001 × ");
        testCalculations("0.0000000000000001 / -2 = -5.e-17", "0.0000000000000001 ÷ ");
        testCalculations("-0.0000000000000001 / -2 = 5.e-17", "-0.0000000000000001 ÷ ");
        testCalculations("0.0000000000000001 - 10 % = 9.e-17", "0.0000000000000001 − 1.e-17");
        testCalculations("0.000005 * -0.0000000000005 = -2.5e-18", "0.000005 × ");
        testCalculations("-0.000005 * 0.0000000000005 = -2.5e-18", "-0.000005 × ");
        testCalculations("0.000005 * 0.0000000000005 = 2.5e-18", "0.000005 × ");
        testCalculations("-0.000005 * -0.0000000000005 = 2.5e-18", "-0.000005 × ");
        testCalculations("0.0000000000005 sqr = 2.5e-25", "sqr(0.0000000000005)");
        testCalculations("-0.0000000000005 sqr = 2.5e-25", "sqr(-0.0000000000005)");
        testCalculations("0.0000000000000001 sqr = 1.e-32", "sqr(0.0000000000000001)");
    }

    private void testCalculations(String expression, String expectedHistory) {
        pushKey(ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String expectedResult = expression.substring(equalSignIndex + 1).trim();

        String[] arguments = expression.substring(0, equalSignIndex).split(ARGUMENT_DELIMITERS);
        enterArguments(arguments);

        WaitForAsyncUtils.waitForFxEvents();
        testHistory(expectedHistory);
        testCalculation(expectedResult);
    }

    private boolean enterArguments(String[] arguments) {
        boolean wasBinaryOperation = false;
        for (int i = 0; i < arguments.length; i++) {
            String argument = arguments[i];
            if (isNumber(argument)) {
                enterNumber(argument);
            } else {
                MathOperation operation = extractOperation(argument);
                if (operation != null) {
                    if (operation.isBinary()) {
                        wasBinaryOperation = true;
                    }
                    fireButton(operation.id());
                } else {
                    fireButton(argument);
                }
            }
        }
        return wasBinaryOperation;
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
    public void testPressingOperationsWithoutFirstNumber() {

        testPressOperation("+ = 0", "0 + ");
        testPressOperation("+ 10 = 10", "0 + ");
        testPressOperation("+ 5 + - = 0", "0 + 5 − ");
        testPressOperation("+ 5 sqr √ = 5", "0 + √(sqr(5))");
        testPressOperation("+ √ = 0", "0 + √(0)");
        testPressOperation("+ √ CE = 0", "0 + ");
        testPressOperation("+ % √ sqr = 0", "0 + sqr(√(0))");
        testPressOperation("+ / - * = 0", "0 × ");
        testPressOperation("+ - - = 0", "0 − ");
        testPressOperation("+ ± 5 / = 1", "0 + 5 ÷ ");
        testPressOperation("+ 55 ⌫ 2 = 52", "0 + ");
        testPressOperation("+ -5 ⌫ = 0", "0 + ");
        testPressOperation("+ 5 sqr CE = 0", "0 + ");

        testPressOperation("- = 0", "0 − ");
        testPressOperation("- 10 = -10", "0 − ");
        testPressOperation("- sqr = 0", "0 − sqr(0)");
        testPressOperation("- sqr √ = 0", "0 − √(sqr(0))");
        testPressOperation("- + / = Result is undefined", "0 ÷ ");
        testPressOperation("- sqr + = 0", "0 − sqr(0) +");
        testPressOperation("- C = 0", "");
        testPressOperation("- % CE = 0", "0 − ");
        testPressOperation("- - % = 0", "0 − 0");
        testPressOperation("- / * 999 ⌫ = 0", "0 × ");
        testPressOperation("- 5 1/ = -0.2", "0 − 1/(5)");
        testPressOperation("- 5 1/ CE = 0", "0 − ");
        testPressOperation("- . % % % = 0", "0 − 0");

        testPressOperation("* = 0", "0 × ");
        testPressOperation("* 10 = 0", "0 × ");
        testPressOperation("* ± = 0", "0 × ");
        testPressOperation("* / + = 0", "0 + ");
        testPressOperation("* sqr = 0", "0 × sqr(0)");
        testPressOperation("* sqr √ = 0", "0 × √(sqr(0))");
        testPressOperation("* ± 5 ± / = 0", "0 × -5 ÷ ");
        testPressOperation("* 55 ⌫ 2 = 0", "0 × ");
        testPressOperation("* -5 ⌫ = 0", "0 × ");
        testPressOperation("* 5 sqr CE = 0", "0 × ");
        testPressOperation("* sqr + = 0", "0 × sqr(0) + ");
        testPressOperation("* C = 0", "");
        testPressOperation("* % CE = 0", "0 × ");

        testPressOperation("/ = Result is undefined", "0 ÷ ");
        testPressOperation("/ 10 = 0", "0 ÷ ");
        testPressOperation("/ % = Result is undefined", "0 ÷ 0");
        testPressOperation("/ sqr = Result is undefined", "0 ÷ sqr(0)");
        testPressOperation("/ sqr √ = Result is undefined", "0 ÷ √(sqr(0))");
        testPressOperation("/ ± 5 ± * = 0", "0 ÷ -5 × ");
        testPressOperation("/ 55 ⌫ 2 = 0", "0 ÷ ");
        testPressOperation("/ -5 ⌫ = Result is undefined", "0 ÷ ");
        testPressOperation("/ 5 sqr CE = Result is undefined", "0 ÷ ");
        testPressOperation("/ sqr sqr = Result is undefined", "0 ÷ sqr(sqr(0))");
        testPressOperation("/ C = 0", "");
        testPressOperation("/ 5 1/ = 0", "0 ÷ 1/(5)");
        testPressOperation("/ 5 1/ CE = Result is undefined", "0 ÷ ");

        testPressOperation("% = 0", "0");
        testPressOperation("5 % = 0", "0");
        testPressOperation(". % = 0", "0");
        testPressOperation("% + = 0", "0 + ");
        testPressOperation("% √ = 0", "√(0)");
        testPressOperation("% % = 0", "0");
        testPressOperation("10 % + % CE = 0", "0 + ");
        testPressOperation("% sqr % = 0", "0");
        testPressOperation("% 21 % = 0", "0");
        testPressOperation("% + - 2 1/ = -0.5", "0 − 1/(2)");
        testPressOperation("% C % = 0", "0");
        testPressOperation("% 2 sqr = 4", "sqr(2)");
        testPressOperation("% ± * 3. * = 0", "0 × 3 × ");

        testPressOperation("√ = 0", "√(0)");
        testPressOperation("√ √ = 0", "√(√(0))");
        testPressOperation("√ sqr = 0", "sqr(√(0))");
        testPressOperation("55 ⌫ 2 √ = 7.211102550927979", "√(52)");
        testPressOperation("-5 ⌫ √ = 0", "√(0)");
        testPressOperation("√ sqr CE = 0", "");
        testPressOperation("2 ± √ = Invalid input", "√(-2)");
        testPressOperation("√ C = 0", "");
        testPressOperation("√ C √ = 0", "√(0)");
        testPressOperation("5 √ 22 = 22", "");
        testPressOperation("√ 1/ = Cannot divide by zero", "1/(√(0))");
        testPressOperation("√ sqr 1/ = Cannot divide by zero", "1/(sqr(√(0)))");
        testPressOperation("√ + 2 √ = 1.414213562373095", "√(0) + √(2)");

        testPressOperation("sqr = 0", "sqr(0)");
        testPressOperation("sqr sqr = 0", "sqr(sqr(0))");
        testPressOperation("sqr 1/ = Cannot divide by zero", "1/(sqr(0))");
        testPressOperation("55 ⌫ 2 sqr = 2,704", "sqr(52)");
        testPressOperation("5 ± ⌫ sqr = 0", "sqr(0)");
        testPressOperation("sqr sqr CE = 0", "");
        testPressOperation("2 ± sqr = 4", "sqr(-2)");
        testPressOperation("sqr C = 0", "");
        testPressOperation("sqr C sqr = 0", "sqr(0)");
        testPressOperation("sqr 22 = 22", "");
        testPressOperation("5 sqr 1/ = 0.04", "1/(sqr(5))");
        testPressOperation("sqr sqr * = 0", "sqr(sqr(0)) × ");
        testPressOperation("sqr + 2 sqr = 4", "sqr(0) + sqr(2)");

        testPressOperation("1/ = Cannot divide by zero", "1/(0)");
        testPressOperation(". 1/ = Cannot divide by zero", "1/(0)");
        testPressOperation("5 1/ = 0.2", "1/(5)");
        testPressOperation("5 1/ ± = -0.2", "1/(5)");
        testPressOperation("5 1/ 1/ = 5", "1/(1/(5))");
        testPressOperation("1/ C 1/ = Cannot divide by zero", "1/(0)");
        testPressOperation("1/ CE 1/ = Cannot divide by zero", "1/(0)");
        testPressOperation("5 1/ + 1/ = 5.2", "1/(5) + 1/(0.2)");
        testPressOperation("5 1/ + 1/ CE = 0.2", "1/(5) + ");
        testPressOperation("5 1/ sqr . sqr = 0", "sqr(0)");
        testPressOperation("5 1/ sqr = 0.04", "sqr(1/(5))");
        testPressOperation("5 1/ 25 1/ = 0.04", "1/(25)");
        testPressOperation("5 1/ % = 0", "0");

        testPressOperation("± = 0", "");
        testPressOperation("± 1/ = Cannot divide by zero", "1/(0)");
        testPressOperation("± ± = 0", "");
        testPressOperation("2 ± ± = 2", "");
        testPressOperation("2 ± 1/ = -0.5", "1/(-2)");
        testPressOperation("2 ± + ± = 0", "-2 + ");
        testPressOperation("2 ± ⌫ = 0", "");
        testPressOperation("2 ± 5 sqr = 625", "sqr(-25)");
        testPressOperation(". 5 ± - . = -0.5", "-0.5 − ");
        testPressOperation("± * = 0", "0 × ");
        testPressOperation("± / * √ CE = 0", "0 × ");
        testPressOperation("± / * √ = 0", "0 × √(0)");
        testPressOperation("± 2 ± 1 ± = 21", "");

        // press point button without entering a digit and verify current number
        pushKey(ESCAPE);
        fireButton(".");
        testCurrentText("0.");
    }

    private void testPressOperation(String expression, String expectedHistory) {
        pushKey(ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String expectedResult = expression.substring(equalSignIndex + 1).trim();
        String[] arguments = expression.substring(0, equalSignIndex).split(ARGUMENT_DELIMITERS);

        boolean wasBinaryOperation = enterArguments(arguments);
        WaitForAsyncUtils.waitForFxEvents();
        testHistory(expectedHistory);
        // if were only unary operations no need to press "=", result is already showed in text field
        testOperationResult(expectedResult, wasBinaryOperation);
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
        testOperationForException("-5 √ = Invalid input", "√(-5)");
        testOperationForException("-5.5 √ = Invalid input", "√(-5.5)");
        testOperationForException("-10000000 √ = Invalid input", "√(-10000000)");
        testOperationForException("-2147483648 √ = Invalid input", "√(-2147483648)");
        testOperationForException("-555555555000005 √ = Invalid input", "√(-555555555000005)");
        testOperationForException("-9223372036854775 √ = Invalid input", "√(-9223372036854775)");
        testOperationForException("-9999999999999999 √ = Invalid input", "√(-9999999999999999)");

        // reverse with zero base
        testOperationForException("0 1/ = Cannot divide by zero", "1/(0)");
        testOperationForException("0.0 1/ = Cannot divide by zero", "1/(0)");
    }

    private void testOperationForException(String expression, String expectedHistory) {
        pushKey(KeyCode.ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String[] expressionParts = expression.substring(0, equalSignIndex).trim().split(ARGUMENT_DELIMITERS);
        String expectedErrorMessage = expression.substring(equalSignIndex + 1).trim();

        boolean wasBinaryOperation = enterArguments(expressionParts);
        // if were only unary operations no need to press "=", result is already showed in text field
        testOperationResult(expectedErrorMessage, wasBinaryOperation);
        testHistory(expectedHistory);
    }

    private void testOperationResult(String expectedResult, boolean wasBinaryOperation) {
        if (wasBinaryOperation) {
            testCalculation(expectedResult);
        } else { // if was no binary operations no need to press result button
            testCurrentText(expectedResult);
        }
    }

    @Test
    public void testMemoryOperations() {
        // add to memorized value
        testMemoryOperation("M+ = 0");
        testMemoryOperation("50 M+ = 50");
        testMemoryOperation("50 M+ -3 M+ = 47");
        testMemoryOperation("50 M+ M+ = 100");
        testMemoryOperation("50 M+ -3 M+ 0.555555 M+ = 47.555555");
        testMemoryOperation("50 M+ -3 M+ 0.555555 M+ -99999999999 M+ = -99,999,999,951.44445");
        testMemoryOperation("50 M+ M- = 0");
        testMemoryOperation("9999999999999999 M+ M- = 0");
        testMemoryOperation("50 M+ M- M- = -50");
        testMemoryOperation("50 M+ 0 MS 3 M- M- = -6");
        testMemoryOperation("M+ MS MS 5 M- = -5");
        testMemoryOperation("90 M+ MC 5 M- = -5");
        testMemoryOperation("90 M+ MR MC M+ = 90");
        testMemoryOperation("10 M+ M+ MR M+ = 40");

        // subtract from memorized value
        testMemoryOperation("M- = 0");
        testMemoryOperation("50 M- = -50");
        testMemoryOperation("50 M- -3 M- = -47");
        testMemoryOperation("50 M- -3 M- 0.555555 M- = -47.555555");
        testMemoryOperation("50 M- -3 M- 0.555555 M- -99999999999 M- = 99,999,999,951.44445");
        testMemoryOperation("50 M- M- = -100");
        testMemoryOperation("50 M- MS = 50");
        testMemoryOperation("50 M- MS M+ = 100");
        testMemoryOperation("50 M- MC MS = 50");
        testMemoryOperation("M- MC MS 2 M- M- = -4");
        testMemoryOperation("M- 5 M- M- = -10");
        testMemoryOperation("2 M- M- MR M- M- = 4");
        testMemoryOperation("2 M- M- MR M+ M+ = -12");
        testMemoryOperation("9999999999999999 M- M+ = 0");

        // store value in memorized
        testMemoryOperation("MS = 0");
        testMemoryOperation("50 MS = 50");
        testMemoryOperation("0.555555 MS = 0.555555");
        testMemoryOperation("-3 MS = -3");
        testMemoryOperation("-99999999999 MS = -99,999,999,999");
        testMemoryOperation("9999999999999999 MS MS = 9,999,999,999,999,999");
        testMemoryOperation("50 MS M+ = 100");
        testMemoryOperation("50 MS M- = 0");
        testMemoryOperation("50 MS MR MC M- = -50");
        testMemoryOperation("50 MS MS M+ = 100");
        testMemoryOperation("50 MS MC M- MS = 50");
        testMemoryOperation("50 MS M+ 5 M+ 150 M- = -45");
        testMemoryOperation("50 MS M+ M+ MR M+ = 300");
        testMemoryOperation("MS MC 2 MS MR 0 M+ = 2");

        fireButton(MEMORY_CLEAN.symbol());
    }

    private void testMemoryOperation(String expression) {
        pushKey(ESCAPE);
        fireButton(MEMORY_CLEAN.symbol());
        WaitForAsyncUtils.waitForFxEvents();

        int equalSignIndex = expression.indexOf("=");
        String expectedMemorizedValue = expression.substring(equalSignIndex + 1).trim();

        String[] expressionParts = expression.substring(0, equalSignIndex).trim().split(ARGUMENT_DELIMITERS);
        for (int i = 0; i < expressionParts.length; i++) {
            String argument = expressionParts[i];
            if (isNumber(argument)) {
                enterNumber(argument);
            } else {
                fireButton(argument);
                testHistory("");
            }
        }

        //reset entered number and check for 0 (default value for new number)
        fireButton(CLEAN_CURRENT.symbol());
        testCurrentText("0");
        WaitForAsyncUtils.waitForFxEvents();
        // recall memorized value
        fireButton(MEMORY_RECALL.symbol());

        WaitForAsyncUtils.waitForFxEvents();
        testCurrentText(expectedMemorizedValue);
        WaitForAsyncUtils.waitForFxEvents();
        pushKey(ESCAPE);
    }

    @Test
    public void testDisablingMemoryButtons() {
        // Memory operations that activates all memory buttons
        testDisablingMemoryButtons(MEMORY_ADD);
        testDisablingMemoryButtons(MEMORY_SUBTRACT);
        testDisablingMemoryButtons(MEMORY_STORE);
    }

    private void testDisablingMemoryButtons(MemoryOperation operation) {
        // Memory clean, recall and show are disabled by default
        testMemoryButtonIsEnable(MEMORY_CLEAN, false);
        testMemoryButtonIsEnable(MEMORY_RECALL, false);
        testMemoryButtonIsEnable(MEMORY_SHOW, false);
        // Memory add, subtract and store are enable by default
        testMemoryButtonIsEnable(MEMORY_ADD, true);
        testMemoryButtonIsEnable(MEMORY_SUBTRACT, true);
        testMemoryButtonIsEnable(MEMORY_STORE, true);

        fireButton(operation.symbol());

        // After pressing any memory button, all memory buttons become enable
        testMemoryButtonIsEnable(MEMORY_CLEAN, true);
        testMemoryButtonIsEnable(MEMORY_RECALL, true);
        testMemoryButtonIsEnable(MEMORY_SHOW, true);
        testMemoryButtonIsEnable(MEMORY_ADD, true);
        testMemoryButtonIsEnable(MEMORY_SUBTRACT, true);
        testMemoryButtonIsEnable(MEMORY_STORE, true);
        // reset everything
        fireButton(MEMORY_CLEAN.symbol());
    }

    private void testMemoryButtonIsEnable(MemoryOperation operation, boolean isEnable) {
        testButtonEnable(operation.symbol(), isEnable);
    }

    private void testButtonEnable(String buttonId, boolean expectedEnable) {
        Button memoryButton = buttons.get(buttonId);
        boolean isButtonEnable = !memoryButton.isDisabled();
        assertEquals(expectedEnable, isButtonEnable);
    }

    @Test
    public void testPressDisabledButtons() {
        // cause an exception by dividing zero by zero to disable buttons with operations
        testOperationForException("0 / 0 = Result is undefined", "0 ÷ ");

        for (MathOperation operation : MathOperation.values()) {
            if (operation != RESULT) {
                testButtonEnable(operation.id(), false);
            }
        }
        for (MemoryOperation operation : MemoryOperation.values()) {
            testButtonEnable(operation.symbol(), false);
        }
        testButtonEnable(POINT, false);
        testButtonEnable("history", false);

        // try to press math operation from keyboard
        testOperationKeyPressedAfterException(KeyCode.EQUALS, SHIFT_DOWN); // +
        testOperationKeyPressedAfterException(KeyCode.MINUS);
        testOperationKeyPressedAfterException(KeyCode.DIVIDE);
        testOperationKeyPressedAfterException(KeyCode.DIGIT8, SHIFT_DOWN); // *
        testOperationKeyPressedAfterException(KeyCode.DIGIT2, SHIFT_DOWN); // √
        testOperationKeyPressedAfterException(KeyCode.DIGIT5, SHIFT_DOWN); // %
        testOperationKeyPressedAfterException(KeyCode.PERIOD); // .
        testOperationKeyPressedAfterException(KeyCode.Q); // sqr
        testOperationKeyPressedAfterException(KeyCode.R); // 1/

        // clean error message
        pushKey(KeyCode.ESCAPE);
        testCurrentText("0");
    }

    private void testOperationKeyPressedAfterException(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        pushKey(keyCode, modifiers);
        testCurrentText(RESULT_IS_UNDEFINED_MESSAGE);
    }

    @Test
    public void testForOverflow() {
        // the lower bound for overflow
        testForOverflow("1.e-9999 / 10 = Overflow", "1.e-9999 ÷ ");
        testForOverflow("1.e-9999 / 100 = Overflow","1.e-9999 ÷ " );
        testForOverflow("1.e-9999 * 0.1 = Overflow", "1.e-9999 × ");
        testForOverflow("1.e-9999 - 1 % = Overflow", "1.e-9999 − 1.e-10001");
        testForOverflow("1.e-9999 sqr = Overflow", "sqr(1.e-9999)");

        // the upper bound for overflow
        testForOverflow("1.e+9999 * 10 = Overflow", "1.e+9999 × ");
        testForOverflow("1.e+9999 * 100 = Overflow", "1.e+9999 × ");
        testForOverflow("1.e+9999 sqr = Overflow", "sqr(1.e+9999)");
        testForOverflow("1.e+9999 / 0.1 = Overflow", "1.e+9999 ÷ ");
        testForOverflow("1.e+9999 + 1000 % = Overflow", "1.e+9999 + 1.e+10000");
    }

    private void testForOverflow(String expression, String expectedHistory) {
        pushKey(KeyCode.ESCAPE);
        int equalSignIndex = expression.indexOf("=");
        String[] expressionParts = expression.substring(0, equalSignIndex).trim().split(ARGUMENT_DELIMITERS);
        String expectedErrorMessage = expression.substring(equalSignIndex + 1).trim();

        // the last valid number before overflow
        String firstNumber = expressionParts[0];

        // divide or multiply 1 to 1000000000 (1.e+9)
        enterNumber("1");
        MathOperation operation;
        if (firstNumber.contains("+")) {
            operation = MULTIPLY;
        } else {
            operation = DIVIDE;
        }
        fireButton(operation.id());
        enterNumber("1000000000");

        // press result button many times to get too large or too small number
        while (!currentNumberText.getText().trim().equals(firstNumber.trim())) {
            pushKey(KeyCode.ENTER);
        }

        // try to get overflow message
        boolean wasBinaryOperation = enterArguments(Arrays.copyOfRange(expressionParts, 1, expressionParts.length));
        if (expression.contains("%")) { // no need to press "=" because exception throws after calculating percentage and "=" resets it to zero
            wasBinaryOperation = false;
        }
        testHistory(expectedHistory);
        // if were only unary operations no need to press "=", result is already showed in text field
        testOperationResult(expectedErrorMessage, wasBinaryOperation);
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
        WaitForAsyncUtils.waitForFxEvents();
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

    private void pushKey(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        KeyCodeCombination combination = new KeyCodeCombination(keyCode, modifiers);
        robot.push(combination);
    }
}