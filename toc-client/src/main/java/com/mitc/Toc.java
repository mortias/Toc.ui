package com.mitc;

import com.mitc.config.Settings;
import com.mitc.javafx.Browser;
import com.mitc.services.vertx.VertxService;
import com.mitc.spring.AppConfig;
import com.mitc.spring.AppContext;
import com.mitc.util.crypto.FileEncryptor;
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

public class Toc extends Application {

    private static Settings settings;
    private static VertxService vertxService;

    private Logger logger = LogManager.getLogger(Toc.class);

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AppContext ctx = (AppContext) context.getBean("AppContext");
        ctx.launch();

        vertxService = ctx.getVertxService();
        settings = ctx.getSettings();

        launch(args);
    }

    @Override
    public void start(Stage stage) throws MalformedURLException {

        Browser browser = new Browser(settings, vertxService);

        Scene scene = new Scene(browser, settings.getWidth(), settings.getHeight(), Color.web("#000000"));
        scene.setFill(Color.TRANSPARENT);

        stage.setOnCloseRequest(we -> {
            FileEncryptor.getInstance().scanFiles(settings.isEncrypted());
            System.exit(0);
        });

        stage.setTitle("Toc.ui - v1.1.0");
        stage.setScene(scene);
        stage.show();

    }

}