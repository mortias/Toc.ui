package com.mitc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Browser extends Region {

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private final static Logger logger = Logger.getLogger(Browser.class);

    public Browser(ResourceBundle lang, String url) {

        webView.setContextMenuEnabled(false);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {

                    EventListener listener = evt -> {
                        try {
                            String[] array = {"cmd", "/C", "start", evt.getCurrentTarget().toString().replace("file:///", "")};
                            logger.info(MessageFormat.format(lang.getString("running.action"), Arrays.toString(array)));
                            Runtime.getRuntime().exec(array);
                        } catch (IOException e) {
                            logger.error(MessageFormat.format(lang.getString("an.error.occured"), e.getLocalizedMessage()));
                        }
                    };

                    // add event listeners to all links
                    Document doc = webEngine.getDocument();
                    NodeList lst = doc.getElementsByTagName("a");
                    for (int i = 0; i != lst.getLength(); i++) {
                        Element el = (Element) lst.item(i);
                        if (!el.toString().contains("#tabs")) {
                            ((EventTarget) el).addEventListener("mouseup", listener, false);
                        }
                    }
                }
            }
        });

        webEngine.load(url);
        getChildren().add(webView);
    }

    public WebView getWebView() {
        return webView;
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
