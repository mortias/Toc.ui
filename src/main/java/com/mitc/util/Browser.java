package com.mitc.util;

import com.mitc.crypto.Crypt;
import com.mitc.crypto.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Browser extends Region {

    public static Crypt crypt = Crypt.getInstance();
    public static Config config = Config.getInstance();

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private final static Logger logger = Logger.getLogger(Browser.class);
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public Browser(String url) {

        logger.setLevel(Level.toLevel(config.getSettings().getLevel()));

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {

                    EventListener listener = evt -> {
                        try {

                            boolean encrypt = config.getSettings().isEncrypted();
                            String target = evt.getCurrentTarget().toString().replace("file:///", "");

                            if (encrypt)
                                target = crypt.decryptFile(new File(target + ".crypt"));

                            if (target != null && target.length() > 0) {

                                String[] array = {"cmd", "/C", "start", target};
                                logger.info(MessageFormat.format(
                                        config.translate("running.action"), Arrays.toString(array)));

                                Runtime.getRuntime().exec(array);

                                if (encrypt) {
                                    // encrypt again after some time
                                    FutureTask task = new FutureTask<>(
                                            new Task(config.getSettings().getTimeout() * 1000, target));
                                    executor.execute(task);
                                }
                            }

                        } catch (IOException e) {
                            logger.error(MessageFormat.format(
                                    config.translate("an.error.occured"), e.getLocalizedMessage()));
                        }
                    };

                    // add event listeners to all links
                    Document doc = webEngine.getDocument();
                    NodeList lst = doc.getElementsByTagName("a");
                    for (int i = 0; i != lst.getLength(); i++) {
                        Element el = (Element) lst.item(i);
                        if (!el.toString().contains("#tabs")) {
                            logger.trace(MessageFormat.format(config.translate("adding.eventlistener"), el.toString()));
                            ((EventTarget) el).addEventListener("mouseup", listener, false);
                        }
                    }
                }
            }
        });

        webView.setContextMenuEnabled(false);
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
