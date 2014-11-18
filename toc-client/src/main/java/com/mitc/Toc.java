package com.mitc;

import com.mitc.crypto.FileEncryptor;
import com.mitc.servers.hawtio.HawtioService;
import com.mitc.servers.rest.RestService;
import com.mitc.servers.system.SystemStatusService;
import com.mitc.servers.vertx.VertxService;
import com.mitc.toc.Browser;
import com.mitc.toc.Config;
import com.mitc.toc.Content;
import com.mitc.toc.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

public class Toc extends Application {

    private static Stage stage;

    public static Config config;
    public static Content content;
    public static Settings settings;

    private static final String templatePath = "site.html";
    private static final String indexPath = "index.html";

    private static final Logger logger = LogManager.getLogger(Toc.class);
    public static FileEncryptor crypt = FileEncryptor.getInstance();

    static {
        config = Config.getInstance();
        content = Content.getInstance();
    }

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

        new RestService(settings);
        new VertxService(settings);

        if (settings.getMonitoring()) {
            new SystemStatusService(settings);
        }

        if (settings.getHawtio()) {
            startHawtIoServer();
        }

        // load the site
        content.load(indexPath, templatePath);

    }

    public static void startHawtIoServer() {
        if (!settings.getHawtio()) {
           new HawtioService(settings);
        }
    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {

        Toc.stage = stage;

        // load the site
        URL url = new File(settings.getRoot() + "site/html/" + indexPath).toURI().toURL();
        logger.info(MessageFormat.format(
                config.translate("browsing.file"), url.toString()));

        stage.setTitle(config.translate("title"));

        Browser browser = new Browser(url.toString(), true);
        Scene scene = new Scene(browser, settings.getWidth(),
                settings.getHeight(), Color.web("#000000"));

        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    public static void reform() {
        stage.setWidth(settings.getWidth());
        stage.setHeight(settings.getHeight());
    }

    @Override
    public void stop() throws IOException, URISyntaxException {
        crypt.init();
    }

}