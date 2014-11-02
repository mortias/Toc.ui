package com.mitc;

import com.mitc.crypto.Crypt;
import com.mitc.rest.server.RESTServer;
import com.mitc.util.Browser;
import com.mitc.util.Config;
import com.mitc.util.Content;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
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
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class Toc extends Application {

    public static Crypt crypt = Crypt.getInstance();
    public static Config config = Config.getInstance();
    public static Content content = Content.getInstance();

    private static final Logger logger = LogManager.getLogger(Toc.class);

    private static final String configPath = "settings.yml";
    private static final String iconImagePath = "icon.png";
    private static final String templatePath = "template.html";
    private static final String indexPath = "index.html";

    public double initialX;
    public double initialY;

    private Executor executor;
    private JaxRsServer jaxRsServer;
    public static Stage stage;

    public static void main(String[] args) throws IOException, java.awt.AWTException, URISyntaxException {
        launch(args);
    }

    @Override
    public void init() throws Exception {

        // load the yml file
        config.load(configPath);

        // uncrypt / decrypt
        crypt.init();

        // setup the rest server
        jaxRsServer = new JaxRsServer();
        executor = new SequentialExecutor();
        executor.execute(jaxRsServer);

        // load the site
        content.load(indexPath, templatePath);

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
        stage.setTitle(config.translate("title"));

        // load the site
        URL url = new File(config.getSettings().getSite() + "html/" + indexPath).toURI().toURL();
        logger.info(MessageFormat.format(
                config.translate("browsing.file"), url.toString()));

        Browser browser = new Browser(url.toString());
        addDraggableNode(browser.getWebView());

        // create the layout for the javafx stage.
        StackPane stackPane = new StackPane(browser);
        stackPane.setPrefSize(config.getSettings().getWidth(),
                config.getSettings().getHeight());

        Scene scene = new Scene(stackPane, config.getSettings().getWidth(),
                config.getSettings().getHeight(), Color.web("#000000"));

        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws IOException, URISyntaxException {
        jaxRsServer.stop();
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

    public Stage getStage() {
        return this.stage;
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


    /* Server runnable clesses */
    class SequentialExecutor implements Executor {
        final Queue<Runnable> queue = new ArrayDeque<Runnable>();
        Runnable task;

        public synchronized void execute(final Runnable r) {
            queue.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        next();
                    }
                }
            });
            if (task == null) {
                next();
            }
        }

        private synchronized void next() {
            if ((task = queue.poll()) != null) {
                new Thread(task).start();
            }
        }
    }

    class JaxRsServer implements Runnable {

        private RESTServer server;

        public JaxRsServer() {
            this.server = new RESTServer();
        }

        @Override
        public void run() {
            this.server.start();
        }

        public void stop() {
            this.server.stop();
        }
    }
}