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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    private VertxService vertxService;
    private static final int NTHREDS = 10;
    private ExecutorService executor;

    private Logger logger = LogManager.getLogger(Browser.class);
    public static FileEncryptor crypt = FileEncryptor.getInstance();

    public Browser() {
        executor = Executors.newFixedThreadPool(NTHREDS);
    }

    public Browser(Settings settings, VertxService vertxService) throws MalformedURLException {

        // load the site
        URL url = new File(settings.getRoot() + "site/html-"+settings.getMode()+"/index.html").toURI().toURL();
        logger.info(MessageFormat.format("Browsing file: {0}", url.toString()));

        if (webView == null)
            webView = new WebView();

        if (webEngine == null)
            webEngine = webView.getEngine();

        this.vertxService = vertxService;

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    EventListener listener = evt -> {
                        String target = evt.getCurrentTarget().toString().replace("file:///", "");
                        if (FilenameUtils.getExtension(target).length() == 0
                                || target.contains("https:") || target.contains("mailto:")
                                || target.contains("http:") || target.contains("ftp:")) {
                            executeTarget(target, settings.isEncrypted());
                        } else {
                            if (settings.isEncrypted()) {
                                String key = FileEncryptor.getInstance().getKey();
                                if (key == null || key.trim().length() == 0) {
                                    getPassPhrase();
                                } else {
                                    Path path = Paths.get(target + ".crypt");
                                    if (path.toFile().exists())
                                        target = crypt.handleFile(path, false);
                                    if (target.length() == 0)
                                        getPassPhrase();
                                    else
                                        executeTarget(target, settings.isEncrypted());
                                }
                            } else {
                                if (target.length() > 0)
                                    executeTarget(target, settings.isEncrypted());
                            }
                        }
                    };

                    Document doc = webEngine.getDocument();
                    NodeList lst = doc.getElementsByTagName("a");
                    for (int i = 0; i != lst.getLength(); i++) {
                        Element el = (Element) lst.item(i);
                        String target = el.toString();
                        if (!target.contains("#tabs") && target.length() > 0) {
                            logger.trace(MessageFormat.format("Adding eventListener to: {0}", target));
                            ((EventTarget) el).addEventListener("click", listener, true);
                        }
                    }
                }
            }

        });

        webEngine.load(String.valueOf(url));
        getChildren().add(webView);
    }

    private void executeTarget(String target, boolean isEncrypted) {
        try {
            String[] array = {"cmd", "/C", "start", target};
            logger.info(MessageFormat.format("Running action: {0}", Arrays.toString(array)));
            Runtime.getRuntime().exec(array);
            if (FilenameUtils.separatorsToSystem(target)
                    .contains(FilenameUtils.separatorsToSystem(crypt.getPath()))) {
                // encrypt again after some time in the bin path
                FutureTask task = new FutureTask<>(new AutoEncrypt(2 * 1000, target, isEncrypted));
                executor.execute(task);
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("An error occured: {0}", e.getLocalizedMessage()));
        }
    }

    private void getPassPhrase() {
        JsonObject msg = new JsonObject();
        msg.putString("action", "showGetKeyDialog");
        this.vertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), msg);
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
