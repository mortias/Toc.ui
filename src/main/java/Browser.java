import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;

public class Browser extends Region {

    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();

    public Browser(String source) {

        URL url = getClass().getResource(source);
        webEngine.load(url.toExternalForm());
        webView.setContextMenuEnabled(false);

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
        getChildren().add(webView);

    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
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
