package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.view.CalculatorApplication;
import javafx.application.Application;

/**
 * The class launches the {@link CalculatorApplication}.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorLauncher {

    /**
     * Launches the {@link CalculatorApplication}.
     *
     * @param args an initial arguments for an application
     */
    public static void main(String[] args) {
        Application.launch(CalculatorApplication.class);
    }
}
