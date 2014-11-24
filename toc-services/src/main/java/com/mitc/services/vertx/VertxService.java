package com.mitc.services.vertx;

import com.mitc.config.Settings;
import com.mitc.services.hawtio.HawtioService;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.util.crypto.FileEncryptor;
import com.mitc.util.net.VerifyUrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component("VertxService")
public class VertxService implements Executor {

    @Autowired
    Settings settings;

    @Autowired
    HawtioService hawtioService;

    private VertxServer vertxServer;

    public VertxService() {
    }

    public void init() {
        vertxServer = new VertxServer();
        execute(vertxServer);
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    public void sendMessage(String name, JsonObject replyMsg) {
        vertxServer.sendMessage(name, replyMsg);
    }

    // embedded server class
    private class VertxServer implements Runnable {

        private int port;
        private Vertx vertx;
        private HttpServer httpServer;
        private SockJSServer sockServer;
        private ExecutorService executor;

        private static final int NTHREDS = 10;
        private final Logger logger = LogManager.getLogger(VertxService.class);

        public VertxServer() {

            vertx = VertxFactory.newVertx();
            port = settings.getVertxPort();
            executor = Executors.newFixedThreadPool(NTHREDS);

            // Let everything through
            JsonArray permitted = new JsonArray();
            permitted.add(new JsonObject());

            httpServer = vertx.createHttpServer();

            sockServer = vertx.createSockJSServer(httpServer);
            sockServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);

            vertx.eventBus().registerHandler(Channel.BO_WRITE_CHANNEL.getName(), new Handler<Message>() {
                @Override
                public void handle(Message event) {

                    JsonObject receivedMsg = new JsonObject(event.body().toString());
                    JsonObject replyMsg = new JsonObject();

                    switch (receivedMsg.getString("action")) {

                        case "sendEncryptionKey":
                            FileEncryptor crypt = FileEncryptor.getInstance();
                            crypt.setKey(receivedMsg.getString("encKey"));
                            crypt.scanFiles(true);
                            break;

                        case "saveCustomSettings":
                            settings.setTheme(receivedMsg.getString("theme"));
                            settings.save();
                            replyMsg.putString("action", "saveCustomSettings");
                            sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);
                            break;

                        case "checkIfHawtIoIsRunning":
                            replyMsg.putString("action", "checkIfHawtIoIsRunning");
                            replyMsg.putBoolean("isRunning", settings.getHawtio());
                            sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);
                            break;

                        case "checkIfHrefIsValid":
                            String target = receivedMsg.getString("target");
                            if (target.contains("http://") || target.contains("https://") & !target.contains("mailto:")) {
                                try {
                                    VerifyUrl veryfyUrl = new VerifyUrl(target, settings);
                                    Future<Map<String,Object>> future = executor.submit(veryfyUrl);
                                    replyMsg = new JsonObject(future.get());
                                    replyMsg.putString("action", "checkIfHrefIsValid");
                                    sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        case "startHawtIoServer":
                            hawtioService.init();
                            break;
                    }
                }
            });
        }

        protected void sendMessage(String channel, JsonObject msg) {
            if (vertx != null) {
                logger.debug(MessageFormat.format("Vertx message: {0}", msg));
                vertx.eventBus().publish(channel, msg);
            }
        }

        @Override
        public void run() {
            httpServer.listen(port);
        }
    }

}

