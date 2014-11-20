package com.mitc.javafx;

import com.mitc.config.Settings;
import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
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
import org.springframework.stereotype.Component;
import org.vertx.java.core.json.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Component("Browser")
public class Browser extends Region {

    private WebView webView;
    private WebEngine webEngine;

    private Logger logger = LogManager.getLogger(Browser.class);
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    public static FileEncryptor crypt = FileEncryptor.getInstance();

    public Browser() {
    }

    public Browser(String url, Settings settings, VertxService vertxService) {

        if (webView == null)
            webView = new WebView();

        if (webEngine == null)
            webEngine = webView.getEngine();

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
                            if (settings.isEncrypted()) {
                                String key = FileEncryptor.getInstance().getKey();
                                if (key == null || key.trim().length() == 0) {
                                    JsonObject msg = new JsonObject();
                                    msg.putString("action", "showGetKeyDialog");
                                    vertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), msg);
                                } else {
                                    Path path = Paths.get(target + ".crypt");
                                    if (path.toFile().exists())
                                        target = crypt.handleFile(path, false);
                                    executeTarget(target);
                                    if (FilenameUtils.separatorsToSystem(target)
                                            .contains(FilenameUtils.separatorsToSystem(crypt.getPath()))) {
                                        // encrypt again after some time in the bin path
                                        FutureTask task = new FutureTask<>(
                                                new AutoEncrypt(2 * 1000, target, settings.isEncrypted()));
                                        executor.execute(task);
                                    }
                                }
                            } else {
                                if (target.length() > 0) {
                                    executeTarget(target);
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

}
