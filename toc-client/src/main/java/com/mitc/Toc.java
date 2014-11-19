package com.mitc;

import com.mitc.spring.AppConfig;
import com.mitc.spring.AppContext;
import com.mitc.util.crypto.FileEncryptor;
import com.mitc.javafx.Browser;
import com.mitc.config.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

public class Toc extends Application {

    private static Stage stage;
    private static Settings settings;

    private Logger logger = LogManager.getLogger(Toc.class);

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AppContext ctx = (AppContext) context.getBean("AppContext");
        ctx.launch();

        settings = ctx.getSettings();

        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {

        Toc.stage = stage;

        // load the site
        URL url = new File(settings.getRoot() + "site/html/index.html").toURI().toURL();
        logger.info(MessageFormat.format("Browsing file: {0}", url.toString()));

        Browser browser = new Browser(url.toString(), settings.isEncrypted(), true);

        Scene scene = new Scene(browser, settings.getWidth(), settings.getHeight(), Color.web("#000000"));
        scene.setFill(Color.TRANSPARENT);

        stage.setOnCloseRequest(we -> {
            FileEncryptor.getInstance().init(settings.isEncrypted());
            System.exit(0);
        });

        stage.setTitle("Table of contents v.1");
        stage.setScene(scene);
        stage.show();

    }

    public static void reform() {
        stage.setWidth(settings.getWidth());
        stage.setHeight(settings.getHeight());
    }

}