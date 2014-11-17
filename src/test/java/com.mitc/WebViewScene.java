package com.mitc;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URISyntaxException;

public class WebViewScene extends Application {

    private Pane root;

    @Override
    public void start(final Stage stage) throws URISyntaxException {

        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();

        // webEngine.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        System.out.println(webEngine.getUserAgent());
        webEngine.load("file:///C:/PrivateWS/javafx-toc/src/test/resources/d3js/index.html");

        Scene scene = new Scene(webView);

        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(600);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }

}