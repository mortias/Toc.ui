import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

// Java 8 code
public class toc extends Application {

    // one icon location is shared between the application tray icon and task bar icon.
    // you could also use multiple icons to allow for clean display of tray icons on hi-dpi devices.
    private static final String iconImageLoc = "images/icon.png";

    // application stage is stored so that it can be shown and hidden based on system tray icon operations.
    private Stage stage;

    public double initialX;
    public double initialY;

    public static void main(String[] args) throws IOException, java.awt.AWTException {
        launch(args);
    }

    // sets up the javafx application.
    // a tray icon is setup for the icon, but the main stage remains invisible until the user
    // interacts with the tray icon.

    @Override
    public void start(Stage stage) {

        // stores a reference to the stage.
        this.stage = stage;

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        // out stage will be translucent, so give it a transparent style.
        stage.initStyle(StageStyle.UNDECORATED);

        Browser browser = new Browser("html/index.html");

        StackPane root = new StackPane();
        root.setId("ROOTNODE");

        Rectangle rect = new Rectangle(350, 610);
        rect.setArcHeight(5.0);
        rect.setArcWidth(5.0);

        browser.setClip(rect);
        root.getChildren().add(browser);

        Scene scene = new Scene(root, 350, 610, Color.web("#eeeeee"));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Hello World !");
        stage.setScene(scene);
        stage.getScene().getStylesheets().setAll(RoundedTest.class.getResource("css/gui.css").toString());
        stage.show();

    }

    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(ClassLoader.getSystemResource(iconImageLoc));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name), 
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("Show TOC");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void addDraggableNode(final Node node) {

        node.setOnMousePressed(me -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                initialX = me.getSceneX();
                initialY = me.getSceneY();
            }
        });

        node.setOnMouseDragged(me -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                node.getScene().getWindow().setX(me.getScreenX() - initialX);
                node.getScene().getWindow().setY(me.getScreenY() - initialY);
            }
        });
    }

    class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        public Browser(String source) {

            URL urlHello = getClass().getResource(source);
            webEngine.load(urlHello.toExternalForm());

            addDraggableNode(browser);

            webEngine.locationProperty().addListener((observableValue, oldLoc, newLoc) -> {

                System.out.println(newLoc);

                try {
                    String[] array = {"cmd", "/C", "start", newLoc.replace("file:///", "")};
                    Runtime.getRuntime().exec(array);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //add the web view to the scene
            getChildren().add(browser);

        }

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }

        @Override
        protected double computePrefWidth(double height) {
            return 750;
        }

        @Override
        protected double computePrefHeight(double width) {
            return 500;
        }
    }

}