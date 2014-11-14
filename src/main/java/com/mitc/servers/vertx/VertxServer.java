package com.mitc.servers.vertx;

import com.mitc.Toc;
import com.mitc.toc.config.Config;
import com.mitc.toc.config.Settings;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class VertxServer implements Runnable {

    private int port;
    private Config config;

    private Vertx vertx;
    private HttpServer httpServer;
    private SockJSServer sockServer;

    private final String boReadChannel = "boReadChannel";
    private final String boWriteChannel = "boWriteChannel";

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

        vertx.eventBus().registerHandler(boWriteChannel, new Handler<Message>() {
            @Override
            public void handle(Message event) {

                JsonObject receivedMsg = new JsonObject(event.body().toString());

                Settings settings = Config.getInstance().getSettings();
                settings.setWidth(Integer.parseInt(receivedMsg.getString("width")));
                settings.setHeight(Integer.parseInt(receivedMsg.getString("height")));
                settings.setTheme(receivedMsg.getString("theme"));

                config.saveSettings();
                Toc.reform();

                JsonObject sendMsg = new JsonObject();
                sendMsg.putString("text", "save successfull");

                sendMessage(boReadChannel, sendMsg);

            }
        });

    }

    public void sendMessage(String channel, JsonObject msg) {
        if (vertx != null) {
            vertx.eventBus().publish(channel, msg);
        }
    }

    @Override
    public void run() {
        httpServer.listen(port);
    }

}

