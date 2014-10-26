import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.Config;
import tools.Utils;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.URISyntaxException;

public class toc extends Application {

    private Utils utils = new Utils();

    private static final String iconImagePath = "javafx/images/icon.png";
    private static final String configPath = "toc/settings/config.yml";

    private static final String templatePath = "toc/html/template.html";
    private static final String indexPath = "toc/html/index.html";

    public double initialX;
    public double initialY;

    private Stage stage;

    public static void main(String[] args) throws IOException, java.awt.AWTException, URISyntaxException {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        utils.loadConfig(configPath);
        utils.prepareContent(indexPath, templatePath);
    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {

        // stores a reference to the stage.
        this.stage = stage;

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        // out stage will be translucent, so give it a transparent style.
        stage.initStyle(StageStyle.UNDECORATED);

        Browser browser = new Browser(indexPath);
        addDraggableNode(browser.getWebView());

        StackPane root = new StackPane();
        root.setId("ROOTNODE");

        root.getChildren().add(browser);

        Scene scene = new Scene(root, utils.config.getWidth(), utils.config.getHeight(), Color.web("#000000"));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    // Sets up a system tray icon for the application.
    private void addAppToTray() {

        try {

            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(ClassLoader.getSystemResource(iconImagePath));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem openItem = new java.awt.MenuItem("Show TOC");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

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

}