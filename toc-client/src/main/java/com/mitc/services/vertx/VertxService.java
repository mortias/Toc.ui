package com.mitc.services.vertx;

import com.mitc.Toc;
import com.mitc.services.vertx.resources.Channel;
import com.mitc.toc.Config;
import com.mitc.toc.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

import java.text.MessageFormat;
import java.util.concurrent.Executor;

public class VertxService implements Executor {

    private static VertxServer vertxServer;

    public VertxService(Settings settings) {
        vertxServer = new VertxServer(settings);
        execute(vertxServer);
    }

    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }

    public static void sendMessage(String name, JsonObject replyMsg) {
        vertxServer.sendMessage(name,replyMsg);
    }

    // embedded server class
    private class VertxServer implements Runnable {

        private int port;
        private Config config;

        private Vertx vertx;
        private HttpServer httpServer;
        private SockJSServer sockServer;

        private final Logger logger = LogManager.getLogger(VertxService.class);

        public VertxServer(Settings settings) {


            config = Config.getInstance();
            vertx = VertxFactory.newVertx();
            port = settings.getVertxPort();

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

                        case "saveCustomSettings":
                            Settings settings = Config.getInstance().getSettings();
                            settings.setWidth(Integer.parseInt(receivedMsg.getString("width")));
                            settings.setHeight(Integer.parseInt(receivedMsg.getString("height")));
                            settings.setTheme(receivedMsg.getString("theme"));

                            config.saveSettings();
                            Toc.reform();

                            replyMsg.putString("action", "saveCustomSettings");
                            break;

                        case "checkIfHawtIoIsRunning":
                            replyMsg.putString("action", "checkIfHawtIoIsRunning");
                            replyMsg.putBoolean("isRunning", config.getSettings().getHawtio());
                            break;

                        case "startHawtIoServer":
                            Toc.startHawtIoServer();
                            break;

                    }

                    sendMessage(Channel.BO_READ_CHANNEL.getName(), replyMsg);

                }
            });

        }

        protected void sendMessage(String channel, JsonObject msg) {
            if (vertx != null) {
                logger.debug(MessageFormat.format(config.translate("vertx.message.received"), msg));
                vertx.eventBus().publish(channel, msg);
            }
        }

        @Override
        public void run() {
            httpServer.listen(port);
        }
    }

}

