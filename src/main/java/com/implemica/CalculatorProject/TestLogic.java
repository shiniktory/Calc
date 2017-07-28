package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.exception.CalculationException;
import com.implemica.CalculatorProject.util.OutputFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLogic {
    public static void main(String[] args) throws CalculationException {

//        Pattern pattern = Pattern.compile("([-]?\\d+[.]\\d*[0-8]*)(9{2,})(e.*)?");

        Pattern pattern = Pattern.compile("([-]?9+[.])(9{2,})(e.*)?");
        String stringWithNumber = "999.9999999999";
        Matcher matcher = pattern.matcher(stringWithNumber);
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            int scale = stringWithNumber.indexOf(matcher.group(2)) - stringWithNumber.indexOf(".") - 1;
            System.out.println(new BigDecimal(stringWithNumber).setScale(scale, BigDecimal.ROUND_UP));
        }

    }
}
