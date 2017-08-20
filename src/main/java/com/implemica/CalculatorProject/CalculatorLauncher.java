package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.view.CalculatorApplication;
import javafx.application.Application;

/**
 * The {@code CalculatorLauncher} class starts the {@link CalculatorApplication} from the
 * {@link CalculatorLauncher#main(String[])} method.
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
