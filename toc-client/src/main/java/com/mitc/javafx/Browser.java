package com.mitc.javafx;

import com.mitc.util.crypto.AutoEncrypt;
import com.mitc.util.crypto.FileEncryptor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Browser extends Region {

    public static FileEncryptor crypt = FileEncryptor.getInstance();

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private Logger logger = LogManager.getLogger(Browser.class);
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public Browser(String url, boolean isEncrypted, boolean handleEvents) {

        if (handleEvents) {
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

                public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                        EventListener listener = evt -> {
                            String target = evt.getCurrentTarget().toString().replace("file:///", "");
                            if (FilenameUtils.getExtension(target).length() == 0
                                    || target.contains("https:") || target.contains("mailto:")
                                    || target.contains("http:") || target.contains("ftp:")) {
                                executeTarget(target);
                            } else {
                                if (isEncrypted)
                                    target = crypt.decryptFile(new File(target + ".crypt"));
                                if (target != null && target.length() > 0) {
                                    executeTarget(target);
                                    if (isEncrypted && target.contains(crypt.getPath())) {
                                        // encrypt again after some time
                                        FutureTask task = new FutureTask<>(
                                                new AutoEncrypt(2 * 1000, target, isEncrypted));
                                        executor.execute(task);
                                    }
                                }
                            }
                        };
                        // add event listeners to all links
                        Document doc = webEngine.getDocument();
                        NodeList lst = doc.getElementsByTagName("a");
                        for (int i = 0; i != lst.getLength(); i++) {
                            Element el = (Element) lst.item(i);
                            if (!el.toString().contains("#tabs") && el.toString().length() > 0) {
                                logger.trace(MessageFormat.format("Adding eventListener to: {0}", el.toString()));
                                ((EventTarget) el).addEventListener("mouseup", listener, false);
                            }
                        }
                    }
                }

                private void executeTarget(String target) {
                    try {
                        String[] array = {"cmd", "/C", "start", target};
                        logger.info(MessageFormat.format("Running action: {0}", Arrays.toString(array)));
                        Runtime.getRuntime().exec(array);
                    } catch (IOException e) {
                        logger.error(MessageFormat.format("An error occured: {0}", e.getLocalizedMessage()));
                    }
                }

            });
        }

        webView.setContextMenuEnabled(false);
        webEngine.load(url);

        getChildren().add(webView);
    }

    @Override
    protected void layoutChildren() {
        List<Node> managed = getManagedChildren();
        for (Node child : managed) {
            layoutInArea(child, getInsets().getLeft(), getInsets().getTop(),
                    getWidth() - getInsets().getLeft() - getInsets().getRight(),
                    getHeight() - getInsets().getTop() - getInsets().getBottom(),
                    0, Insets.EMPTY, true, true, HPos.CENTER, VPos.CENTER);
        }
    }

    public WebView getWebView() {
        return webView;
    }

}
