package com.mitc;

import com.mitc.common.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class Toc extends Application {

    private Utils utils = new Utils();

    private static final String configPath = "config.yml";

    private static final String iconImagePath = "icon.png";
    private static final String templatePath = "template.html";
    private static final String indexPath = "index.html";

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
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(utils.lang.getString("title"));

        Browser browser = new Browser(utils.config.getSite() + "html/" + indexPath);
        addDraggableNode(browser.getWebView());

        // create the layout for the javafx stage.
        StackPane stackPane = new StackPane(browser);
        stackPane.setPrefSize(utils.config.getWidth(), utils.config.getHeight());

        Scene scene = new Scene(stackPane, utils.config.getWidth(), utils.config.getHeight(), Color.web("#000000"));
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
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
                System.out.println(utils.lang.getString("errTraySupport"));
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            ImageIcon ico = new ImageIcon(this.getClass().getResource("/images/" + iconImagePath));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(ico.getImage());

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);

        } catch (java.awt.AWTException e) {
            System.out.println(utils.lang.getString("errTrayInit"));
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