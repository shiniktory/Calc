package com.implemica.CalculatorProject.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.implemica.CalculatorProject.model.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.model.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.model.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isDigit;
import static com.implemica.CalculatorProject.view.CalculatorApplication.ApplicationBorder.*;
import static javafx.scene.text.FontWeight.*;

/**
 * The class is an entry point to the application. Configures the {@link Stage} that will be shown to user by adding a
 * customized view and listeners for window transformations.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorApplication extends Application {

    /**
     * A path to the view fxml file.
     */
    private static final String CALCULATOR_VIEW_FILE = "/com/implemica/CalculatorProject/view/calculatorView.fxml";

    /**
     * A path to the file with stylesheets.
     */
    private static final String CSS_FILE = "com/implemica/CalculatorProject/view/winCalc.css";

    /**
     * A path to the icon image.
     */
    private static final String ICON_FILE = "com/implemica/CalculatorProject/view/icon.png";

    /**
     * The name of the application.
     */
    private static final String APPLICATION_NAME = "Calculator";

    /**
     * The minimum height of an application's window.
     */
    private static final double MIN_HEIGHT = 385.0;

    /**
     * The minimum width of an application's window.
     */
    private static final double MIN_WIDTH = 220.0;

    /**
     * The primary {@link Stage} for current application, onto which the application {@link Scene} is set.
     */
    private Stage currentStage;

    /**
     * The {@link Parent} from FXML-file that contains all application's UI elements.
     */
    private Parent root;

    /**
     * The value of pixel count from application's left border to X coordinate where was generated {@link MouseEvent}
     * after mouse pressed on application title {@link Label}.
     */
    private double moveDeltaX;

    /**
     * The value of pixel count from application's top border to Y coordinate where was generated {@link MouseEvent}
     * after mouse pressed on application title {@link Label}.
     */
    private double moveDeltaY;

    /**
     * The X coordinate of mouse position which is the coordinate of left border of {@link Stage} with minimal width.
     */
    private double maxResizeX;

    /**
     * The Y coordinate of mouse position which is the coordinate of top border of {@link Stage} with minimal height.
     */
    private double maxResizeY;

    /**
     * The value of padding size from the application borders where resize events occurs.
     */
    private static final int RESIZE_PADDING = 2;

    /**
     * The flag variable shows is window moving now. Using for excluding conflicts with resizing events.
     */
    private boolean isWindowMoving;

    /**
     * The flag variable shows is window expanded on full screen.
     */
    private boolean isFullScreen;

    /**
     * The {@link Bounds} holds saved information about window position, width and height before expanding on
     * full screen.
     */
    private Bounds savedBoundsBeforeFullScreen;

    /**
     * The value of {@link ApplicationBorder} from what side resize event started.
     */
    private ApplicationBorder borderForResize;

    /**
     * The coordinates of point where current resize {@link MouseEvent} occurred.
     */
    private Point2D currentEventPoint;

    /**
     * The difference between previous and current X coordinate where {@link MouseEvent} occurred.
     */
    private double resizeDeltaX;

    /**
     * The difference between previous and current Y coordinate where {@link MouseEvent} occurred.
     */
    private double resizeDeltaY;

    /**
     * The information about {@link Screen} where application runs.
     */
    private static final Rectangle2D SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds();

    /**
     * The coordinates of mouse position where was generated the previous {@link MouseEvent}.
     */
    private Point2D previousMouseEventPoint;

    /**
     * The default {@link Font} family name for {@link TextField}.
     */
    private static final String TEXTFIELD_FONT_NAME = "Segoe UI Semibold";

    /**
     * The string value of id for the {@link TextField} with current number.
     */
    private static final String CURRENT_NUMBER_TEXTFIELD_ID = "#currentNumberText";

    /**
     * The string value of the font id for {@link Button}s with digits and decimal separator.
     */
    private static final String FONT_ID_FOR_NUMBERS = "numbers";

    /**
     * The string value of the id for {@link Button} with decimal separator.
     */
    private static final String POINT_BUTTON_ID = "point";

    /**
     * The string value of the id for {@link Button} with equal sign that indicates result calculation.
     */
    private static final String CALCULATE_RESULT_BUTTON_ID = "RESULT";

    /**
     * The value of id for the {@link GridPane} with {@link Button}s with numbers and mathematical operations.
     */
    private static final String PANE_WITH_BUTTONS_ID = "#numbers_operations";

    /**
     * The string value of the id for {@link Label} with application title.
     */
    private static final String TITLE_LABEL_ID = "#appTitle";

    /**
     * The string value of id for {@link Button} responsible for expanding window.
     */
    private static final String EXPAND_BUTTON_ID = "#expand";

    /**
     * The string value of id for {@link Button} responsible for hiding window.
     */
    private static final String HIDE_BUTTON_ID = "#hide";

    /**
     * The string value of id for {@link Button} responsible for closing window.
     */
    private static final String CLOSE_BUTTON_ID = "#close";

    /**
     * The title of {@link Alert} window with occurred error information.
     */
    private static final String ERROR_WINDOW_TITLE = "Error";

    /**
     * An error message about {@link Parent} from fxml-file cannot be loaded.
     */
    private static final String CANNOT_LOAD_PARENT_MESSAGE = "Cannot load fxml-file with view. ";

    /**
     * A reference to the {@link TextField} with the current number.
     */
    private TextField currentNumberTextField;

    /**
     * The list of {@link Button} ids associated with an arrays contains minimum, medium and maximum values
     * of an appropriate {@link Button} font size.
     */
    private static final Map<String, Double[]> fontSizes = new LinkedHashMap<>();

    /**
     * The list of {@link Button}s contains {@link Label}s.
     */
    private static final List<String> labeledButtons = new ArrayList<>();

    static {
        // add button's ids and its fonts
        fontSizes.put(PERCENT.name(), new Double[]{16.0, 19.0, 22.0});
        fontSizes.put(SQUARE_ROOT.name(), new Double[]{16.0, 17.0, 22.0});
        fontSizes.put(SQUARE.name(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(REVERSE.name(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(NEGATE.name(), new Double[]{25.0, 29.0, 36.0});
        fontSizes.put(DIVIDE.name(), new Double[]{24.0, 32.0, 42.0});
        fontSizes.put(MULTIPLY.name(), new Double[]{15.0, 19.0, 25.0});
        fontSizes.put(ADD.name(), new Double[]{24.0, 33.0, 42.0});
        fontSizes.put(SUBTRACT.name(), new Double[]{24.0, 35.0, 42.0});
        fontSizes.put(CALCULATE_RESULT_BUTTON_ID, new Double[]{24.0, 34.0, 42.0});

        fontSizes.put(FONT_ID_FOR_NUMBERS, new Double[]{15.0, 20.0, 25.0});

        fontSizes.put(LEFT_ERASE.name(), new Double[]{15.0, 20.0, 25.0});
        fontSizes.put(CLEAN_CURRENT.name(), new Double[]{14.0, 15.0, 23.0});
        fontSizes.put(CLEAN.name(), new Double[]{14.0, 15.0, 23.0});

        // add main textfield and its fonts
        fontSizes.put(CURRENT_NUMBER_TEXTFIELD_ID, new Double[]{23.0, 42.0, 66.0});

        // add buttons with label ids
        labeledButtons.add(NEGATE.name());
        labeledButtons.add(DIVIDE.name());
        labeledButtons.add(MULTIPLY.name());
        labeledButtons.add(SUBTRACT.name());
        labeledButtons.add(ADD.name());
        labeledButtons.add(CALCULATE_RESULT_BUTTON_ID);
        labeledButtons.add(LEFT_ERASE.name());
    }

    /**
     * Configures the {@link Stage} instance and shows the configured application window.
     *
     * @param primaryStage an instance to configure for the current application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            currentStage = primaryStage;
            configureStageParameters();

            // add listeners
            addWindowMoveListener();
            addFullScreenListener();
            addControlButtonsListeners();
            addResizeListeners();

            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(CANNOT_LOAD_PARENT_MESSAGE + e.getMessage());
        }
    }

    /**
     * Configures current {@link Stage} parameters: sets {@link Scene}, title, window style, width and height attributes
     * and etc.
     *
     * @throws IOException if if something wrong with the view file
     */
    private void configureStageParameters() throws IOException {
        // set scene
        root = loadParent();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_FILE);
        currentStage.setScene(scene);

        // set window decoration
        currentStage.setTitle(APPLICATION_NAME);
        currentStage.getIcons().add(new Image(ICON_FILE));
        currentStage.initStyle(StageStyle.UNDECORATED);

        // set window size parameters
        currentStage.setMinHeight(MIN_HEIGHT);
        currentStage.setMinWidth(MIN_WIDTH);
        currentStage.setMaxHeight(SCREEN_BOUNDS.getHeight());
        currentStage.setMaxWidth(SCREEN_BOUNDS.getWidth());
        currentStage.setResizable(true);
    }

    /**
     * Loads and returns the root {@link Parent} for the view.
     *
     * @return the root {@link Parent} for the view
     * @throws IOException if something wrong with the view file
     */
    private Parent loadParent() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        return loader.load(getClass().getResourceAsStream(CALCULATOR_VIEW_FILE));
    }

    /**
     * Shows window with information about {@link Exception} occurred while starting an application.
     *
     * @param errorMessage an error message with information about {@link Exception} occurred
     */
    private static void showErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_WINDOW_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    /**
     * Adds listeners for window move events.
     */
    private void addWindowMoveListener() {
        Label titleLabel = (Label) root.lookup(TITLE_LABEL_ID);
        // save initial parameters
        titleLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveDeltaX = currentStage.getX() - event.getScreenX();
                moveDeltaY = currentStage.getY() + RESIZE_PADDING - event.getScreenY();
                isWindowMoving = true;
                event.consume();
            }
        });

        // move window
        titleLabel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double currentEventX = event.getScreenX();
                double currentEventY = event.getScreenY();

                if (isWindowMoving && currentEventY <= SCREEN_BOUNDS.getMaxY()) {
                    currentStage.setX(currentEventX + moveDeltaX);
                    currentStage.setY(currentEventY + moveDeltaY);
                }
            }
        });
    }

    /**
     * Adds a listener for double click on title panel to expand window on full screen.
     */
    private void addFullScreenListener() {
        Label titleLabel = (Label) root.lookup(TITLE_LABEL_ID);
        titleLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int clickCount = event.getClickCount();
                if (clickCount == 2) {
                    setFullScreen();
                }
            }
        });
    }

    /**
     * Adds listeners for {@link ActionEvent} on control {@link Button}s: close, expand and hide.
     */
    private void addControlButtonsListeners() {
        Button expandButton = (Button) root.lookup(EXPAND_BUTTON_ID);
        expandButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setFullScreen();
            }
        });

        Button hideButton = (Button) root.lookup(HIDE_BUTTON_ID);
        hideButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentStage.setIconified(!currentStage.isIconified());
            }
        });

        Button closeButton = (Button) root.lookup(CLOSE_BUTTON_ID);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentStage.close();
            }
        });
    }

    /**
     * Expand application window on full screen or restore saved before full screen window position, width and height.
     */
    private void setFullScreen() {
        if (!isFullScreen) {
            savedBoundsBeforeFullScreen = new BoundingBox(currentStage.getX(), currentStage.getY(), currentStage.getWidth(), currentStage.getHeight());
            setNewStageParameters(0, 0, currentStage.getMaxWidth(), currentStage.getMaxHeight());

        } else {
            setNewStageParameters(savedBoundsBeforeFullScreen.getMinX(), savedBoundsBeforeFullScreen.getMinY(),
                    savedBoundsBeforeFullScreen.getWidth(), savedBoundsBeforeFullScreen.getHeight());
        }
        isFullScreen = !isFullScreen;
    }

    /**
     * Sets the specified coordinates, width and height to the current {@link Stage}.
     *
     * @param x      an x coordinate of top left application corner
     * @param y      an y coordinate of top left application corner
     * @param width  a value of width to set
     * @param height a value of height to set
     */
    private void setNewStageParameters(double x, double y, double width, double height) {
        if (x != currentStage.getX() || y != currentStage.getY() ||
                width != currentStage.getWidth() || height != currentStage.getHeight()) { // if changed any parameter
            currentStage.setX(x);
            currentStage.setY(y);
            setNewWidth(width);
            setNewHeight(height);
        }
    }

    /**
     * Verifies new width value is in allowed bounds and sets an application width equal the specified value.
     *
     * @param newWidth a new value of width
     */
    private void setNewWidth(double newWidth) {
        if (newWidth >= MIN_WIDTH && newWidth <= currentStage.getMaxWidth()) {
            currentStage.setWidth(newWidth);
        }
    }

    /**
     * Verifies new height value is in allowed bounds and sets an application height equal the specified value.
     *
     * @param newHeight a new value of application height
     */
    private void setNewHeight(double newHeight) {
        if (newHeight >= MIN_HEIGHT && newHeight <= currentStage.getMaxHeight()) {
            currentStage.setHeight(newHeight);
        }
    }

    /**
     * Adds listeners for resize {@link MouseEvent}s.
     */
    private void addResizeListeners() {
        // set cursor style when mouse hover borders
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Bounds currentStageBounds = new BoundingBox(currentStage.getX(), currentStage.getY(),
                        currentStage.getWidth(), currentStage.getHeight());
                Point2D currentMousePosition = new Point2D(event.getScreenX(), event.getScreenY());
                ApplicationBorder applicationBorder = ApplicationBorder.getMousePosition(currentMousePosition, currentStageBounds);

                changeCursorStyle(applicationBorder);
            }
        });

        // save init parameters before resize
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveResizeEventInitParameters(event);
                event.consume();
            }
        });

        // resize window
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleResizeEvent(event);
            }
        });

        // add listeners for window resizing to change content font sizes
        currentStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleButtonFontSize();
            }
        });

        currentStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleButtonFontSize();
            }
        });

        // add listeners for textfield and its content resizing
        currentNumberTextField = (TextField) root.lookup(CURRENT_NUMBER_TEXTFIELD_ID);
        currentNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                scaleTextFieldFont(currentNumberTextField.getBoundsInLocal().getWidth());
            }
        });

        currentNumberTextField.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleTextFieldFont(newValue.doubleValue());
            }
        });
    }

    /**
     * Changes {@link Cursor} style depends on at what {@link ApplicationBorder} current mouse position is.
     */
    private void changeCursorStyle(ApplicationBorder applicationBorder) {
        if (isFullScreen) {
            return;
        }
        Cursor newCursorType;

        if (applicationBorder == TOP_LEFT_CORNER || applicationBorder == BOTTOM_RIGHT_CORNER) {
            newCursorType = Cursor.NW_RESIZE;
        } else if (applicationBorder == LEFT_EDGE || applicationBorder == RIGHT_EDGE) {
            newCursorType = Cursor.H_RESIZE;
        } else if (applicationBorder == BOTTOM_LEFT_CORNER || applicationBorder == TOP_RIGHT_CORNER) {
            newCursorType = Cursor.NE_RESIZE;
        } else if (applicationBorder == BOTTOM_EDGE || applicationBorder == TOP_EDGE) {
            newCursorType = Cursor.V_RESIZE;
        } else {
            newCursorType = Cursor.DEFAULT;
        }

        setCursor(newCursorType);
    }

    /**
     * Changes {@link Cursor} style to the given one.
     *
     * @param cursor a new {@link Cursor} style
     */
    private void setCursor(Cursor cursor) {
        currentStage.getScene().setCursor(cursor);
    }

    /**
     * Saves an information about coordinates where {@link MouseEvent} occurred, current window position,
     * window width and height and other information needed for handling resize events.
     *
     * @param event a {@link MouseEvent} to extract initial information about application
     */
    private void saveResizeEventInitParameters(MouseEvent event) {
        Bounds savedBoundsBeforeResize = new BoundingBox(currentStage.getX(), currentStage.getY(),
                currentStage.getWidth(), currentStage.getHeight());
        Point2D initMouseEventPoint = new Point2D(event.getScreenX(), event.getScreenY());
        previousMouseEventPoint = new Point2D(event.getScreenX(), event.getScreenY());

        // calculate maximum coordinates after what stage width and height be minimal
        maxResizeX = savedBoundsBeforeResize.getMaxX() - currentStage.getMinWidth() - RESIZE_PADDING;
        maxResizeY = savedBoundsBeforeResize.getMaxY() - currentStage.getMinHeight() - RESIZE_PADDING;

        // save init mouse position where resize event starts
        borderForResize = ApplicationBorder.getMousePosition(initMouseEventPoint, savedBoundsBeforeResize);
        if (borderForResize == TOP_LEFT_CORNER ||
                borderForResize == TOP_EDGE) {
            isWindowMoving = false;
        }
    }

    /**
     * Handles {@link MouseEvent}s by resizing window.
     *
     * @param event a {@link MouseEvent} to handle
     */
    private void handleResizeEvent(MouseEvent event) {
        if (borderForResize == null || isFullScreen) { // if event occurred not on the borders or window is on full screen now
            return;
        }
        // set initial values
        currentEventPoint = new Point2D(event.getScreenX(), event.getScreenY());
        resizeDeltaX = 0;
        resizeDeltaY = 0;
        double newStageX = currentStage.getX();
        double newStageY = currentStage.getY();

        // calculate difference between previous and current mouse positions
        if (borderForResize == TOP_LEFT_CORNER && !isWindowMoving) { // do not resize if window move event started
            newStageX = resizeLeftSideByX();
            newStageY = resizeTopSideByY();

        } else if (borderForResize == LEFT_EDGE) {
            newStageX = resizeLeftSideByX();

        } else if (borderForResize == BOTTOM_LEFT_CORNER) {
            newStageX = resizeLeftSideByX();
            newStageY = resizeBottomSideByY();

        } else if (borderForResize == BOTTOM_EDGE) {
            newStageY = resizeBottomSideByY();

        } else if (borderForResize == BOTTOM_RIGHT_CORNER) {
            newStageY = resizeBottomSideByY();
            resizeRightSideByX();

        } else if (borderForResize == RIGHT_EDGE) {
            resizeRightSideByX();

        } else if (borderForResize == TOP_RIGHT_CORNER) {
            newStageY = resizeTopSideByY();
            resizeRightSideByX();

        } else if (borderForResize == TOP_EDGE && !isWindowMoving) {
            newStageY = resizeTopSideByY();
        }

        // calculate and set new stage parameters: width, height, position
        double newWidth = currentStage.getWidth() + resizeDeltaX;
        double newHeight = currentStage.getHeight() + resizeDeltaY;
        setNewStageParameters(newStageX, newStageY, newWidth, newHeight);

        // save current event coordinates
        previousMouseEventPoint = new Point2D(event.getScreenX(), event.getScreenY());
        event.consume();
    }

    /**
     * Returns new X coordinate for application window. Updates difference between previous and current
     * {@link MouseEvent} X coordinate.
     *
     * @return new X coordinate for application window
     */
    private double resizeLeftSideByX() {
        double currentStagePositionX = currentStage.getX();
        double currentEventPositionX = currentEventPoint.getX();
        double newStageX;

        if (currentEventPositionX >= SCREEN_BOUNDS.getMinX() &&
                currentEventPositionX <= maxResizeX) {
            // resize left side only if new x coordinate is between minimal x on screen and
            // x coordinate of point that is window coordinate with min width
            resizeDeltaX = previousMouseEventPoint.getX() - currentEventPositionX;
            newStageX = currentStagePositionX - resizeDeltaX;
        } else {
            newStageX = currentStagePositionX;
        }

        return newStageX;
    }

    /**
     * Returns new Y coordinate for application window. Updates difference between previous and current
     * {@link MouseEvent} Y coordinate.
     *
     * @return new Y coordinate for application window
     */
    private double resizeTopSideByY() {
        double currentStagePositionY = currentStage.getY();
        double currentEventPositionY = currentEventPoint.getY();
        double newStageY;

        if (currentEventPositionY >= SCREEN_BOUNDS.getMinY() &&
                currentEventPositionY <= maxResizeY) {
            // resize top side only if new y coordinate is between minimal y on screen and
            // y coordinate of point that is window coordinate with min height
            resizeDeltaY = previousMouseEventPoint.getY() - currentEventPositionY;
            newStageY = currentStagePositionY - resizeDeltaY;
        } else {
            newStageY = currentStagePositionY;
        }

        return newStageY;
    }

    /**
     * Updates difference between previous and current {@link MouseEvent} X coordinate.
     */
    private void resizeRightSideByX() {
        double rightXForMinWidth = currentStage.getX() + currentStage.getMinWidth();
        double currentEventPositionX = currentEventPoint.getX();

        if (currentEventPositionX >= rightXForMinWidth &&
                currentEventPositionX <= SCREEN_BOUNDS.getMaxX()) {
            // resize right side only if new x coordinate of right edge is between this coordinate for min width and
            // maximum x on screen
            resizeDeltaX = currentEventPositionX - previousMouseEventPoint.getX();
        }
    }

    /**
     * Updates difference between previous and current {@link MouseEvent} Y coordinate. Returns new y coordinate
     * of current window. If bottom window edge y coordinate reached bottom screen y coordinate need to resize window
     * height on maximum and move by y to 0. In other cases window's y coordinate will not change.
     *
     * @return new y coordinate of current window
     */
    private double resizeBottomSideByY() {
        double currentEventPositionY = currentEventPoint.getY();
        double newStagePositionY;

        if (currentEventPositionY >= SCREEN_BOUNDS.getMaxY()) { // if bottom edge y coordinate reached bottom screen y coordinate
            currentStage.setHeight(currentStage.getMaxHeight());
            newStagePositionY = 0;
        } else {
            double bottomYForMinHeight = currentStage.getY() + currentStage.getMinHeight();

            if (currentEventPositionY >= bottomYForMinHeight &&
                    currentEventPositionY <= SCREEN_BOUNDS.getMaxY()) {
                // resize bottom side only if new y coordinate of bottom edge is between this coordinate for min height and
                // maximum y on screen
                resizeDeltaY = currentEventPositionY - previousMouseEventPoint.getY();
            }

            newStagePositionY = currentStage.getY();
        }

        return newStagePositionY;
    }

    /**
     * Changes font size on {@link Button}s depends on window size to avoid {@link Button}'s text overflow.
     */
    private void scaleButtonFontSize() {
        GridPane pane = (GridPane) root.lookup(PANE_WITH_BUTTONS_ID);

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonId = button.getId();

            double newFontSize = getFontSize(buttonId, getFontBoundIndex());
            setButtonFontSize(button, newFontSize);
        }
    }

    /**
     * Changes font size in the {@link TextField} with current entered number depends on window and content sizes
     * to avoid text truncating.
     *
     * @param currentWidth current window width value
     */
    private void scaleTextFieldFont(double currentWidth) {
        Font font = getFontForTextField(currentWidth);
        currentNumberTextField.setFont(font);
        Platform.runLater(() -> currentNumberTextField.end());
    }

    /**
     * Returns a {@link Font} for the {@link TextField} with current number with calculated font size to fit text to
     * the {@link TextField}.
     *
     * @param currentWidth current window width value
     * @return a {@link Font} for the {@link TextField} with current number with calculated font size to fit text to
     * the {@link TextField}
     */
    private Font getFontForTextField(double currentWidth) {
        int defaultFontSizeIndex = getFontBoundIndex();
        double defaultFontSize = fontSizes.get(CURRENT_NUMBER_TEXTFIELD_ID)[defaultFontSizeIndex];
        Font defaultFont = findFontForTextField(defaultFontSize);

        Text text = new Text(currentNumberTextField.getText());
        text.setFont(defaultFont);

        // calculate scale
        double textWidth = text.getLayoutBounds().getWidth();
        double scale = currentWidth / textWidth - 0.1;
        double newFontSize;

        if (scale < 1.0) { // if need to reduce a font size
            newFontSize = defaultFont.getSize() * scale;
        } else {
            newFontSize = defaultFontSize;
        }

        return findFontForTextField(newFontSize);
    }

    /**
     * Finds and returns {@link Font} instance that has {@link #TEXTFIELD_FONT_NAME} family,
     * {@link FontWeight#BOLD} and the given size.
     *
     * @param fontSize a value of font size to apply to the current font
     * @return {@link Font} instance that has {@link #TEXTFIELD_FONT_NAME} family,
     * {@link FontWeight#BOLD} and the given size
     */
    private Font findFontForTextField(double fontSize) {
        return Font.font(TEXTFIELD_FONT_NAME, BOLD, fontSize);
    }

    /**
     * Returns an index of bound calculated based on window width and height.
     * Example: 0 - smallest window size bound, 1 - medium, 2 - largest.
     *
     * @return an index of bound calculated based on window width and height
     */
    private int getFontBoundIndex() {
        /*
                        min         middle      max
            width       200         280+        550+
            height      370         480+        640+
         */
        double currentWidth = currentStage.getWidth();
        double currentHeight = currentStage.getHeight();

        if (currentWidth < 280 || currentHeight < 480) {
            return 0;
        }

        if (currentWidth > 550 && currentHeight > 640) {
            return 2;
        }

        return 1;
    }

    /**
     * Returns font size for the element with specified id and index of current window size bound.
     *
     * @param elementId  a value of element id to get font size for
     * @param boundIndex an index of current window size bound
     * @return font size for the element with specified id and index of current window size bound
     */
    private double getFontSize(String elementId, int boundIndex) {
        if (isDigit(elementId) || POINT_BUTTON_ID.equals(elementId)) { // get font size for buttons with digits or point
            elementId = FONT_ID_FOR_NUMBERS;
        } else {
            elementId = elementId.toUpperCase();
        }

        return fontSizes.get(elementId)[boundIndex];
    }

    /**
     * Sets the given font size for the specified {@link Button}'s content.
     *
     * @param button      a {@link Button} to change content font size
     * @param newFontSize a value of a new font size
     */
    private void setButtonFontSize(Button button, double newFontSize) {
        String buttonId = button.getId().toUpperCase();
        Labeled labeledElement;

        if (labeledButtons.contains(buttonId)) { // if button is labeled get label
            labeledElement = (Label) button.getChildrenUnmodifiable().get(0);
        } else {
            labeledElement = button;
        }

        Font newFont = new Font(labeledElement.getFont().getFamily(), newFontSize);
        labeledElement.setFont(newFont);
    }

    /**
     * The enum contains mouse positions on the application borders.
     */
    enum ApplicationBorder {
        // Constants represent each window border positions.
        TOP_LEFT_CORNER, LEFT_EDGE, BOTTOM_LEFT_CORNER, BOTTOM_EDGE, BOTTOM_RIGHT_CORNER, RIGHT_EDGE,
        TOP_RIGHT_CORNER, TOP_EDGE;

        /**
         * Returns {@link ApplicationBorder} where the specified mouse coordinates are or
         * null if mouse coordinates are not at the borders.
         *
         * @param mouseEventPoint   a {@link Point2D} instance contains coordinates where {@link MouseEvent} was generated
         * @param applicationBounds a current application {@link Bounds}
         * @return {@link ApplicationBorder} where the specified mouse coordinates are
         * or null if coordinates are not at the borders
         */
        static ApplicationBorder getMousePosition(Point2D mouseEventPoint, Bounds applicationBounds) {

            // calculate distance from the event coordinates to application edges
            double distanceToTopEdge = mouseEventPoint.getY() - applicationBounds.getMinY();
            double distanceToLeftEdge = mouseEventPoint.getX() - applicationBounds.getMinX();
            double distanceToBottomEdge = applicationBounds.getMaxY() - mouseEventPoint.getY();
            double distanceToRightEdge = applicationBounds.getMaxX() - mouseEventPoint.getX();

            ApplicationBorder applicationBorder;

            if (distanceToLeftEdge <= RESIZE_PADDING && distanceToTopEdge <= RESIZE_PADDING) {
                applicationBorder = TOP_LEFT_CORNER;
            } else if (distanceToTopEdge <= applicationBounds.getHeight() - RESIZE_PADDING && distanceToLeftEdge <= RESIZE_PADDING) {
                applicationBorder = LEFT_EDGE;
            } else if (distanceToLeftEdge <= RESIZE_PADDING && distanceToBottomEdge <= RESIZE_PADDING) {
                applicationBorder = BOTTOM_LEFT_CORNER;
            } else if (distanceToLeftEdge <= applicationBounds.getWidth() - RESIZE_PADDING && distanceToBottomEdge <= RESIZE_PADDING) {
                applicationBorder = BOTTOM_EDGE;
            } else if (distanceToRightEdge <= RESIZE_PADDING && distanceToBottomEdge <= RESIZE_PADDING) {
                applicationBorder = BOTTOM_RIGHT_CORNER;
            } else if (distanceToTopEdge <= applicationBounds.getHeight() - RESIZE_PADDING && distanceToRightEdge <= RESIZE_PADDING) {
                applicationBorder = RIGHT_EDGE;
            } else if (distanceToTopEdge <= RESIZE_PADDING && distanceToRightEdge <= RESIZE_PADDING) {
                applicationBorder = TOP_RIGHT_CORNER;
            } else if (distanceToLeftEdge <= applicationBounds.getWidth() - RESIZE_PADDING && distanceToTopEdge < RESIZE_PADDING) {
                applicationBorder = TOP_EDGE;
            } else {
                applicationBorder = null;
            }

            return applicationBorder;
        }
    }
}
