package com.mitc;

import com.mitc.crypto.FileEncryptor;
import com.mitc.servers.hawtio.HawtioServer;
import com.mitc.servers.rest.RestServer;
import com.mitc.servers.vertx.VertxServer;
import com.mitc.toc.config.Config;
import com.mitc.toc.config.Settings;
import com.mitc.toc.javafx.Browser;
import com.mitc.toc.javafx.Content;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.Executor;

public class Toc extends Application {

    private static final Logger logger = LogManager.getLogger(Toc.class);

    private static final String iconImagePath = "icon.png";
    private static final String templatePath = "template.html";
    private static final String indexPath = "index.html";

    public static FileEncryptor crypt = FileEncryptor.getInstance();

    private static Stage stage;
    private double initialX;
    private double initialY;

    public static Config config;
    public static Content content;
    public static Settings settings;

    static {
        config = Config.getInstance();
        content = Content.getInstance();
    }

    private Executor executor;

    public static void main(String[] args) throws IOException, java.awt.AWTException, URISyntaxException {
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void init() throws Exception {

        // load the yml file
        config.loadSettings();
        settings = config.getSettings();

        // encrypt / decrypt
        crypt.init();

        executor = new ThreadPerTaskExecutor();
        executor.execute(new RestServer(settings));
        executor.execute(new VertxServer(settings));
        executor.execute(new HawtioServer(settings));

        // load the site
        content.load(indexPath, templatePath);

    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {

        // stores a reference to the stage.
        Toc.stage = stage;

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        // load the site
        URL url = new File(settings.getRoot() + "site/html/" + indexPath).toURI().toURL();
        logger.info(MessageFormat.format(
                config.translate("browsing.file"), url.toString()));

        Browser browser = new Browser(url.toString(), true);

        if (settings.getUndecorated()) {
            // out stage will be translucent, so give it a transparent style.
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(config.translate("title"));
            addDraggableNode(browser.getWebView());
        }

        Scene scene = new Scene(browser, settings.getWidth(),
                settings.getHeight(), Color.web("#000000"));

        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    public static void reform(){
        stage.setWidth(settings.getWidth());
        stage.setHeight(settings.getHeight());
    }

    @Override
    public void stop() throws IOException, URISyntaxException {
        crypt.init();
    }

    // Sets up a system tray icon for the application.
    private void addAppToTray() {
        try {

            // ensure awt toolkit is initialized.
            Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!SystemTray.isSupported()) {
                logger.error(config.translate("errTraySupport"));
                Platform.exit();
            }

            // set up a system tray icon.
            SystemTray tray = SystemTray.getSystemTray();
            ImageIcon ico = new ImageIcon(this.getClass().getResource("/images/" + iconImagePath));
            TrayIcon trayIcon = new TrayIcon(ico.getImage());

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final PopupMenu popup = new PopupMenu();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);

        } catch (AWTException e) {
            logger.error(config.translate("errTrayInit"));
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

    class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

}